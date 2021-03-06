package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import buravel.buravel.modules.bookmark.dtos.BookmarkDto;
import buravel.buravel.modules.bookmark.dtos.BookmarkResponseDto;
import buravel.buravel.modules.bookmark.dtos.UserBookmarkDto;
import buravel.buravel.modules.errors.ErrorResource;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkValidatior bookmarkValidatior;

    @GetMapping
    public ResponseEntity getBookmarkList(@CurrentUser Account account){

        CollectionModel<EntityModel<BookmarkResponseDto>> entityModels = bookmarkService.findAllBookmark(account);
        CollectionModel<EntityModel<BookmarkResponseDto>> response = bookmarkService.addLinkWithBookmarks(entityModels);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity createBookmark(@RequestBody @Valid BookmarkDto bookmarkDto, @CurrentUser Account account, Errors errors){
        if (errors.hasErrors()) {
            EntityModel<Errors> error1 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error1);
        }
        //if user already has same named bookmark, get error
        UserBookmarkDto userBookmarkDto = new UserBookmarkDto(bookmarkDto,account);
        bookmarkValidatior.validate(userBookmarkDto,errors);
        if(errors.hasErrors()){
            EntityModel<Errors> error2 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error2);
        }

        BookmarkResponseDto bookmarkResponseDto = bookmarkService.createBookmark(bookmarkDto,account);
        EntityModel<BookmarkResponseDto> responseResource = BookmarkResource.modelOf(bookmarkResponseDto);
        responseResource.add(linkTo(BookmarkController.class).withRel("getBookmarkList"));
        responseResource.add(linkTo(BookmarkController.class).slash(bookmarkResponseDto.getId()).withRel("deleteBookmark"));
        responseResource.add(linkTo(BookmarkController.class).slash(bookmarkResponseDto.getId()).withRel("modifyBookmark"));


        return ResponseEntity.ok(responseResource);
    }

    @DeleteMapping(value = "/{bookmark_id}")
    public ResponseEntity deleteBookmark(@PathVariable Long bookmark_id, @CurrentUser Account account) throws NotFoundException {
        URI location = linkTo(BookmarkController.class).withRel("getBookmarkList").toUri();
        if (!bookmarkRepository.existsById(bookmark_id)) {
            return ResponseEntity.notFound().build();
        }
        bookmarkService.deleteBookmark(bookmark_id,account);
        return ResponseEntity.ok(location);

    }

    @PatchMapping(value = "/{bookmark_id}")
    public ResponseEntity modifyBookmarkTitle(@PathVariable Long bookmark_id,@RequestBody @Valid BookmarkDto bookmarkDto,@CurrentUser Account account,Errors errors) throws NotFoundException{

        if (!bookmarkRepository.existsById(bookmark_id)) {
            return ResponseEntity.notFound().build();
        }

        if(errors.hasErrors()) {
            EntityModel<Errors> error1 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error1);
        }
        UserBookmarkDto userBookmarkDto = new UserBookmarkDto(bookmarkDto, account);
        bookmarkValidatior.validate(userBookmarkDto,errors);
        if(errors.hasErrors()){
            EntityModel<Errors> error2 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error2);
        }

        URI location = linkTo(BookmarkController.class).withRel("getBookmarkList").toUri();
        BookmarkResponseDto bookmarkResponseDto = bookmarkService.modifyBookmark(bookmark_id,bookmarkDto,account);
        EntityModel<BookmarkResponseDto> responseResource = BookmarkResource.modelOf(bookmarkResponseDto);

        responseResource.add(linkTo(BookmarkController.class).withRel("getBookmarkList"));
        responseResource.add(linkTo(BookmarkController.class).slash(bookmarkResponseDto.getId()).withRel("deleteBookmark"));
        responseResource.add(linkTo(BookmarkController.class).slash(bookmarkResponseDto.getId()).withRel("modifyBookmark"));

        return ResponseEntity.ok(location);
    }
}
