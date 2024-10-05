package com.ahmetymtkn.connectversenew;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmetymtkn.connectversenew.databinding.FragmentAiChatPageBinding;
import com.ahmetymtkn.connectversenew.model.Message;

import java.util.ArrayList;

public class AiChatPage extends Fragment {

    FragmentAiChatPageBinding binding;
    private static final String apikey = "AIzaSyCB83m3UcnZtqZTbY4a7GEjp5iAlBww0DI";
    private MessageAdapter chatAdapter;
    private ArrayList<Message> messageList;
    private GeminiResp geminiResp;

    public AiChatPage() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentAiChatPageBinding.inflate(inflater, container, false);

        messageList = new ArrayList<>();
        chatAdapter = new MessageAdapter(messageList, "user",getContext());
        binding.recyclerview.setAdapter(chatAdapter);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        geminiResp = new GeminiResp();
        String instructionMessage = "Sen bir chat uygulamasısın ve ismin ConnectversAI. Eğer sana birisi ne olduğunu sorarsa ayrıntı veremeyeceksin. Sadece benim sana söylediklerimi yapacaksın. İnsanlar sana kim olduğunu sorarsa ismin ";
        sendInstructionToAI(instructionMessage);

        binding.messagesend.setOnClickListener(v -> {
            String userMessage = binding.sendMessageEditText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                messageList.add(new Message(userMessage, "user"));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                binding.recyclerview.scrollToPosition(messageList.size() - 1);
                binding.sendMessageEditText.setText("");
                geminiResp.getResponse(geminiResp.getModel(), userMessage, new ResponseCallBack() {
                    @Override
                    public void onResponse(String responseText) {
                        if (responseText.contains("Gemini") || responseText.contains("detay")) {
                            responseText = "Bu konuda bilgi veremem.";
                        }

                        messageList.add(new Message(responseText, "ai"));
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        binding.recyclerview.scrollToPosition(messageList.size() - 1);
                    }

                    @Override
                    public void onError(Throwable error) {
                    }
                });
            }
        });

        return binding.getRoot();
    }

    private void sendInstructionToAI(String instructionMessage) {
        geminiResp.getResponse(geminiResp.getModel(), instructionMessage, new ResponseCallBack() {
            @Override
            public void onResponse(String response) {
                System.out.println("AI Instruction Response: " + response);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("AI Instruction Error: " + t.getMessage());
            }
        });
    }
}

