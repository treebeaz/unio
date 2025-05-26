package ru.unio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.unio.entity.Chat;
import ru.unio.entity.Message;
import ru.unio.entity.User;
import ru.unio.repository.ChatRepository;
import ru.unio.repository.MessageRepository;
import ru.unio.repository.UserRepository;
import ru.unio.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private final UserService userService;

    @GetMapping
    public String chatListPage(@AuthenticationPrincipal User user, Model model) {
        List<Chat> chats = chatRepository.findByUser1OrUser2(user, user);
        model.addAttribute("chats", chats);
        model.addAttribute("user", user);
        return "chat_list";
    }

    @GetMapping("/{chatId}")
    public String openChat(@PathVariable Long chatId,
                           @AuthenticationPrincipal User user,
                           Model model) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        List<Message> messages = messageRepository.findByChatOrderByTimestampAsc(chat);

        List<Chat> chats = chatRepository.findByUser1OrUser2(user, user);

        model.addAttribute("chats", chats);
        model.addAttribute("currentChat", chat);
        model.addAttribute("messages", messages);
        model.addAttribute("user", user);
        return "chat_list";
    }

    @PostMapping("/{chatId}/send")
    public String sendMessage(@PathVariable Long chatId,
                              @AuthenticationPrincipal User sender,
                              @RequestParam("content") String content) {
//        User user = sender.ge;
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
        return "redirect:/chats/" + chatId;
    }
}