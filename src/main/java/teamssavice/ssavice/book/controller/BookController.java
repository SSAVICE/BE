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
import teamssavice.ssavice.global.annotation.CurrentAuth;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.Auth;
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
            @CurrentAuth Auth authUser,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam BookStatusFilter status
    ) {

        BookCommand.RetrieveByStatus command = BookCommand.RetrieveByStatus.of(authUser.id(), pageable, status);

        Page<BookModel.Info> models = bookService.getMyBooksByStatus(command);
        Page<BookResponse.Info> responsePage = models.map(BookResponse.Info::from);

        return ResponseEntity.ok(PageResponse.from(responsePage));
    }

    @GetMapping("/user/summary")
    @RequireRole(Role.USER)
    public ResponseEntity<BookResponse.Count> getBookSummary(
            @CurrentAuth Auth authUser
    ) {
        BookModel.Count model = bookService.getBookSummary(authUser.id());

        return ResponseEntity.ok(BookResponse.Count.from(model));
    }

    @PostMapping("/{serviceId}/apply")
    @RequireRole(Role.USER)
    public ResponseEntity<BookResponse.Apply> applyServiceItem(
            @CurrentAuth Auth authUser,
            @PathVariable Long serviceId
    ) {
        BookModel.Apply model = bookService.apply(authUser.id(), serviceId);
        return ResponseEntity.ok(BookResponse.Apply.from(model));
    }


    @PostMapping("/{serviceId}/cancel")
    @RequireRole(Role.USER)
    public ResponseEntity<Void> cancelParticipation(
            @CurrentAuth Auth authUser,
            @PathVariable Long serviceId
    ) {
        bookService.cancel(ServiceItemCommand.Cancel.of(authUser.id(), serviceId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/book/{service-id}/participant")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<PageResponse<BookResponse.Participant>> getParticipants(
        @CurrentAuth Auth authCompany,
        @PathVariable("service-id") Long serviceItemId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<BookModel.Participant> models = bookService.getParticipants(authCompany.id(), serviceItemId,
            pageable);
        Page<BookResponse.Participant> responsePage = models.map(BookResponse.Participant::from);

        return ResponseEntity.ok(PageResponse.from(responsePage));
    }
}
