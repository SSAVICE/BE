package teamssavice.ssavice.chatmember.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.chatmember.controller.dto.ChatMemberResponse;
import teamssavice.ssavice.chatmember.service.ChatMemberService;
import teamssavice.ssavice.chatmember.service.dto.ChatMemberModel;
import teamssavice.ssavice.global.annotation.RequireRole;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-members")
public class ChatMemberController {

    private final ChatMemberService chatMemberService;

    @RequireRole({Role.USER, Role.COMPANY})
    @GetMapping("")
    public ResponseEntity<ChatMemberResponse.Members> getMembers(
        @RequestParam List<Long> ids
    ) {
        List<ChatMemberModel.MemberInfo> members = chatMemberService.getMemberInfos(ids);
        return ResponseEntity.ok(ChatMemberResponse.Members.from(members));
    }
}
