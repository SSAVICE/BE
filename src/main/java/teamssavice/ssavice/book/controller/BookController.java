package teamssavice.ssavice.book.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.book.constants.BookStatusFilter;
import teamssavice.ssavice.book.controller.dto.BookResponse;
import teamssavice.ssavice.book.service.BookService;
import teamssavice.ssavice.book.service.dto.BookCommand;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.PageResponse;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;


@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/user")
    @RequireRole(Role.USER)
    public ResponseEntity<PageResponse<BookResponse.Info>> getMyBooksByStatus(
        @CurrentId Long userId,
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam BookStatusFilter status
    ) {

        BookCommand.RetrieveByStatus command = BookCommand.RetrieveByStatus.of(userId, pageable, status);

        Page<BookModel.Info> models = bookService.getMyBooksByStatus(command);
        Page<BookResponse.Info> reponsePage = models.map(BookResponse.Info::from);

        return ResponseEntity.ok(PageResponse.from(reponsePage));
    }

    @GetMapping("/user/summary")
    @RequireRole(Role.USER)
    public ResponseEntity<BookResponse.BookSummary> getBookSummary(
        @CurrentId Long userId
    ) {
        BookModel.BookSummary model = bookService.getBookSummary(userId);

        return ResponseEntity.ok(BookResponse.BookSummary.from(model));
    }

    @PostMapping("/{serviceId}/apply")
    @RequireRole(Role.USER)
    public ResponseEntity<BookResponse.Apply> applyServiceItem(
            @CurrentId Long userId,
            @PathVariable Long serviceId
    ) {
        BookModel.Apply model = bookService.apply(userId, serviceId);
        return ResponseEntity.ok(BookResponse.Apply.from(model));
    }


    @PostMapping("/{serviceId}/cancel")
    @RequireRole(Role.USER)
    public ResponseEntity<Void> cancelParticipation(
            @CurrentId Long userId,
            @PathVariable Long serviceId
    ) {
        bookService.cancel(ServiceItemCommand.Cancel.of(userId, serviceId));
        return ResponseEntity.ok().build();
    }
}
