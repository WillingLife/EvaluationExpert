package com.smartcourse.infra.ai;

import com.smartcourse.infra.ai.dto.AiQueryQuestionDTO;
import com.smartcourse.infra.ai.vo.AiQueryQuestionVO;
import com.smartcourse.pojo.dto.exam.stream.AiStreamPayload;
import com.smartcourse.properties.ExamPromptsProperties;
import com.smartcourse.service.AiToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.function.Function;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(ExamPromptsProperties.class)
public class ExamAiAgent {
    private static final String GENERATING_EVENT = "generating";
    private final LlmKernelService llmKernelService;
    private final AiToolService aiToolService;
    private final ExamPromptsProperties examPromptsProperties;

    /**
     * 发起 AI 生成请求
     * @param context 上下文数据
     * @param eventSink 用于工具回调发送 SSE 消息的接收器
     * @return 原始的 ChatResponse Flux
     */
    public Flux<ChatResponse> callAiStream(ExamGenerationContext context, Sinks.Many<AiStreamPayload> eventSink) {
        // 1. 定义工具 (此时逻辑分离了，清晰很多)
        ToolCallback toolCallback = buildQueryTool(eventSink);

        // 2. 构建 Prompt
        PromptTemplate systemTemplate = new PromptTemplate(examPromptsProperties.getSystem());
        PromptTemplate userTemplate = new PromptTemplate(examPromptsProperties.getUser());

        Map<String, Object> sysVars = Map.of("criteria", context.getFormatInstructions());
        Map<String, Object> userVars = Map.of(
                "user_prompt", context.getUserPrompt(),
                "selected_question_json",context.getSelectedQuestionJson(),
                "knowledge_json", context.getKnowledgeJson(),
                "course_id", context.getCourseId(),
                "context",context.getContext()
        );

        String finalSystemPrompt = systemTemplate.render(sysVars);
        String finalUserPrompt = userTemplate.render(userVars);

        // 3. 调用 LLM
        return llmKernelService.streamChat(finalSystemPrompt, finalUserPrompt, toolCallback);
    }

    // 将工具定义抽取为私有方法
    private ToolCallback buildQueryTool(Sinks.Many<AiStreamPayload> sink) {
        Function<AiQueryQuestionDTO, AiQueryQuestionVO> toolFunction = request ->
                aiToolService.queryQuestionTool(request,
                        msg -> sink.tryEmitNext(new AiStreamPayload(GENERATING_EVENT, msg))
                );
        return FunctionToolCallback
                .builder("queryQuestionTool", toolFunction)
                .description("根据查询题库中的题目")
                .inputType(AiQueryQuestionDTO.class)
                .build();
    }

}
