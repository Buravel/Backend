package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import buravel.buravel.modules.bookmarkPost.BookmarkPostController;
import buravel.buravel.modules.errors.ErrorResource;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.swing.text.html.parser.Entity;
import javax.validation.Valid;
import java.net.URI;
import java.util.Collection;
import java.util.List;

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

        CollectionModel<EntityModel<BookmarkResponseDto>> response = bookmarkService.findAllBookmark(account);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity createBookmark(@RequestBody @Valid BookmarkDto bookmarkDto,@CurrentUser Account account, Errors errors){
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

        return ResponseEntity.ok(responseResource);
    }

    @DeleteMapping(value = "/{bookmark_id}")
    public ResponseEntity deleteBookmark(@PathVariable Long bookmark_id, @CurrentUser Account account){

        try{
            bookmarkService.deleteBookmark(bookmark_id,account);
            URI location = linkTo(BookmarkController.class).withRel("get-list").toUri();
            return ResponseEntity.ok(location);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build(); //not found bookmark
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().build(); //invalid user
        }
    }

    @PatchMapping(value = "/{bookmark_id}")
    public ResponseEntity modifyBookmarkTitle(@PathVariable Long bookmark_id,@RequestBody @Valid BookmarkDto bookmarkDto,@CurrentUser Account account,Errors errors){
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
        try{
            bookmarkService.deleteBookmark(bookmark_id,account);
            BookmarkResponseDto bookmarkResponseDto = bookmarkService.createBookmark(bookmarkDto,account);
            URI location = linkTo(BookmarkPostController.class).slash(bookmarkResponseDto.getId()).withSelfRel().toUri();
            return ResponseEntity.ok(location);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
