package ru.unio.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.unio.entity.Chat;
import ru.unio.entity.Message;
import ru.unio.entity.User;
import ru.unio.repository.ChatRepository;
import ru.unio.repository.MessageRepository;
import ru.unio.service.UserService;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/chats")
public class ChatController {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;

    public ChatController(ChatRepository chatRepository,
                          MessageRepository messageRepository,
                          UserService userService) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @GetMapping
    public String chatListPage(@AuthenticationPrincipal User user, Model model) {
        List<Chat> chats = chatRepository.findByUser1OrUser2(user, user);
        model.addAttribute("chats", chats);
        model.addAttribute("user", user);
        return "/chats/chat-list";
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
        return "/chats/chat-list";
    }

    @PostMapping("/{chatId}/send")
    public String sendMessage(@PathVariable Long chatId,
                              @AuthenticationPrincipal User sender,
                              @RequestParam("content") String content) {
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