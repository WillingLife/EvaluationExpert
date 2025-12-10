package com.smartcourse.infra.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class LlmKernelService {
    private final ChatClient chatClient;

    public LlmKernelService(ChatClient.Builder builder, @Qualifier("deepseekChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem("deepseek").build();
    }

    public Flux<ChatResponse> streamChat(String userMessage, String... toolNames) {
        return chatClient.prompt()
                .user(userMessage)
                // 【修改点】这里是最关键的！
                // 新版本的 prompt() 链式调用中，方法名变成了 .tools()
                // 它可以直接接收 Bean 的名称字符串
                .toolNames(toolNames)
                .stream()
                .chatResponse();
    }

    public Flux<ChatResponse> streamChat(String systemPrompt, String userMessage, String... toolNames) {
        return chatClient.prompt()
                .system(systemPrompt) // 覆盖默认的 System Prompt
                .user(userMessage)    // 设置本次的用户问题
                .toolNames(toolNames)    // 挂载工具
                .stream()
                .chatResponse();
    }
    public Flux<ChatResponse> streamChat(String systemPrompt, String userMessage) {
        return chatClient.prompt()
                .system(systemPrompt) // 覆盖默认的 System Prompt
                .user(userMessage)    // 设置本次的用户问题// 挂载工具
                .stream()
                .chatResponse();
    }

    public Flux<ChatResponse> streamChat(String systemPrompt, String userMessage, ToolCallback... toolCallbacks) {
        return chatClient.prompt()
                .system(systemPrompt) // 覆盖默认的 System Prompt
                .user(userMessage)    // 设置本次的用户问题
                .toolCallbacks(toolCallbacks)    // 挂载工具
                .stream()
                .chatResponse();
    }
}
