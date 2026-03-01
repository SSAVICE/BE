package teamssavice.ssavice.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.chat.controller.dto.ChatResponse;
import teamssavice.ssavice.chat.service.ChatService;
import teamssavice.ssavice.chat.service.dto.ChatModel;
import teamssavice.ssavice.global.annotation.CurrentAuth;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.Auth;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-members")
public class ChatMemberController {

    private final ChatService chatService;

    @RequireRole({Role.USER, Role.COMPANY})
    @GetMapping("/rooms/{roomId}/members")
    public ResponseEntity<ChatResponse.Members> getRoomMembers(
        @PathVariable String roomId,
        @CurrentAuth Auth auth
    ) {
        List<ChatModel.MemberInfo> members = chatService.getRoomMemberInfos(roomId, auth.id());
        return ResponseEntity.ok(ChatResponse.Members.from(members));
    }
}
