package com.smartcourse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourse.pojo.dto.exam.TeacherExamAiGenerateDTO;
import com.smartcourse.pojo.dto.exam.stream.AiStreamPayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // 1. 启动完整的 Spring 上下文，注入所有 Bean
class ExamServiceTest {

    @Autowired
    private TeacherExamService examService;
    @Autowired// 注入你的高层 Service
    private  ObjectMapper objectMapper;

//    @Test
//    void testAiGenerateExam() {
//        System.out.println(">>> 开始测试流式生成...");
//
//        // 2. 调用方法获取 Flux
//        Flux<ChatResponse> responseFlux = examService.aiGenerateExam();
//
//        // 3. 订阅并打印 (核心逻辑)
//        responseFlux
//                .doOnNext(response -> {
//                    // Spring AI 返回的 ChatResponse 包含了 generation 信息
//                    // 注意：流式返回时，content 可能是 null (比如第一帧只有 metadata)
//                    if (response.getResult() != null && response.getResult().getOutput().getText() != null) {
//                        // print 不换行，模拟打字机效果
//                        System.out.print(response.getResult().getOutput().getText());
//                    }
//                })
//                .doOnError(e -> {
//                    System.err.println("\n>>> 发生错误: " + e.getMessage());
//                    e.printStackTrace();
//                })
//                .doOnComplete(() -> {
//                    System.out.println("\n>>> 生成结束");
//                })
//                // 4. 重要：阻塞主线程直到流结束
//                // 如果没有这行，测试方法会瞬间执行完，后台线程会被杀掉，你看不到输出
//                .blockLast();
//    }
//
//    @Test
//    void testAiGenerateExamWithReasoning() {
//        examService.aiGenerateExam()
//                .doOnNext(response -> {
//                    var result = response.getResult();
//                    if (result == null) return;
//
//                    var output = result.getOutput(); // 这里就是 AssistantMessage
//
//                    // --- 1. 提取思考内容 (Reasoning) ---
//                    // 注意：请根据你 debug 看到的实际 Key 填写，通常是 "reasoningContent" 或 "reasoning_content"
//                    // 假设你看到的是 "reasoningContext"
//                    Object reasoning = output.getMetadata().get("reasoningContent");
//
//                    if (reasoning != null && !reasoning.toString().isEmpty()) {
//                        // 使用 ANSI 转义码打印黄色文字，代表“思考过程”
//                        System.out.print("\u001B[33m" + reasoning + "\u001B[0m");
//                        System.out.flush();
//                    }
//
//                    // --- 2. 提取最终正文 (Content) ---
//                    String content = output.getText();
//                    if (content != null) {
//                        // 正常打印白色文字
//                        System.out.print(content);
//                    }
//                })
//                .blockLast();
//    }

    @Test
    void testAiGenerateExamStreamStyle() {
        TeacherExamAiGenerateDTO dto = new TeacherExamAiGenerateDTO();
        examService.aiGenerateExam(dto)
                .doOnNext(this::printPayload) // 在流的过程中打印
                .blockLast(); // 【关键】阻塞直到最后一个元素完成
    }
    private void printPayload(AiStreamPayload payload) {
        System.out.println("\n========================================");
        System.out.println("EVENT: " + payload.getEvent());
        System.out.print("DATA : ");

        Object data = payload.getData();

        if (data == null) {
            System.out.println("null");
            return;
        }

        try {
            // 如果本来就是 String，直接打印（比如思考过程的文本）
            if (data instanceof String str) {
                System.out.println(str);
            }
            // 否则（比如题目列表 List<VO>），转 JSON 打印
            else {
                String json = objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(data);
                System.out.println(json);
            }
        } catch (Exception e) {
            System.err.println("[序列化失败] " + data.toString());
            e.printStackTrace();
        }
    }
}