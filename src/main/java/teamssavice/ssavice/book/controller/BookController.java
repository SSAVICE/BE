package teamssavice.ssavice.book.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.book.controller.dto.BookResponse;
import teamssavice.ssavice.book.service.BookService;
import teamssavice.ssavice.book.service.dto.BookCommand;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@RequireRole(Role.USER)
public class BookController {

    private final BookService bookService;

    @GetMapping("/book")
    public ResponseEntity<Page<BookResponse.Info>> getMyBook(
            @CurrentId Long userId,
            @PageableDefault(size = 10) Pageable pageable
    ) {

        System.out.println(userId);
        BookCommand.Retrieve command = BookCommand.Retrieve.of(userId, pageable);

        Page<BookModel.Info> models = bookService.getMyBooks(command);

        return ResponseEntity.ok(models.map(BookResponse.Info::from));
    }
}
