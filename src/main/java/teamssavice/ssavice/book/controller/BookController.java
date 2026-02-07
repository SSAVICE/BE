package teamssavice.ssavice.book.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.book.constants.BookStatusFilter;
import teamssavice.ssavice.book.controller.dto.BookResponse;
import teamssavice.ssavice.book.service.BookService;
import teamssavice.ssavice.book.service.dto.BookCommand;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.PageResponse;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/user/book")
    @RequireRole(Role.USER)
    public ResponseEntity<PageResponse<BookResponse.Info>> getMyBooksByStatus(
        @CurrentId Long userId,
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam BookStatusFilter status
    ) {

        BookCommand.RetrieveByStatus command = BookCommand.RetrieveByStatus.of(userId, pageable,
            status);

        Page<BookModel.Info> models = bookService.getMyBooksByStatus(command);
        Page<BookResponse.Info> responsePage = models.map(BookResponse.Info::from);

        return ResponseEntity.ok(PageResponse.from(responsePage));
    }

    @GetMapping("/user/book/summary")
    @RequireRole(Role.USER)
    public ResponseEntity<BookResponse.BookSummary> getBookSummary(
        @CurrentId Long userId
    ) {
        BookModel.BookSummary model = bookService.getBookSummary(userId);

        return ResponseEntity.ok(BookResponse.BookSummary.from(model));
    }

    @GetMapping("/company/book")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<PageResponse<BookResponse.Info>> getMyCompanysBooksByStatus(
        @CurrentId Long companyId,
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam BookStatusFilter status
    ) {
        BookCommand.RetrieveByStatus command = BookCommand.RetrieveByStatus.of(companyId, pageable,
            status);

        Page<BookModel.Info> models = bookService.getMyCompanysBooksByStatus(command);
        Page<BookResponse.Info> responsePage = models.map(BookResponse.Info::from);

        return ResponseEntity.ok(PageResponse.from(responsePage));
    }

    @GetMapping("/company/book/summary")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<BookResponse.BookSummary> getCompanyBookSummary(
        @CurrentId Long companyId
    ) {
        BookModel.BookSummary model = bookService.getCompanysBookSummary(companyId);

        return ResponseEntity.ok(BookResponse.BookSummary.from(model));
    }

    @GetMapping("/book/{service-id}/participant")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<PageResponse<BookResponse.Participant>> getParticipants(
        @CurrentId Long companyId,
        @PathVariable("service-id") Long serviceItemId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<BookModel.Participant> models = bookService.getParticipants(companyId, serviceItemId,
            pageable);
        Page<BookResponse.Participant> responsePage = models.map(BookResponse.Participant::from);

        return ResponseEntity.ok(PageResponse.from(responsePage));
    }
}
