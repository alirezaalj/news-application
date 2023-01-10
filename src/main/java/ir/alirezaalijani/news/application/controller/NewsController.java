package ir.alirezaalijani.news.application.controller;

import ir.alirezaalijani.news.application.domain.error.exception.BadRequestFieldException;
import ir.alirezaalijani.news.application.domain.error.exception.InternalServerException;
import ir.alirezaalijani.news.application.domain.request.NewsRequest;
import ir.alirezaalijani.news.application.domain.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Alireza Alijani : <a href="https://alirezaalijani.ir">https://alirezaalijani.ir</a>
 * @email alirezaalijani.ir@gmail.com
 * @date 12/8/2022
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1")
public class NewsController {
    private final NewsService newsService;

    @GetMapping("news")
    public ResponseEntity<?> getNews( @RequestParam(name = "size", defaultValue = "10") Integer size,
                                      @RequestParam(name = "on", defaultValue = "0") Integer on){
        Pageable page = PageRequest.of(on, size);
        return ResponseEntity.ok(newsService.allNews(page));
    }

    @GetMapping("news/{id}")
    public ResponseEntity<?> getNews(@PathVariable Long id){
        return ResponseEntity.ok(newsService.getNews(id));
    }

    @PostMapping("news")
    public ResponseEntity<?> createNews(@Valid @RequestBody NewsRequest news, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new BadRequestFieldException("news has errors",bindingResult);
        }
        var response= newsService.createNews(news);
        if (response!=null){
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        System.out.println("create news failed");
        throw new InternalServerException(this.getClass(),"unknown error");
    }

    @PutMapping("news")
    public ResponseEntity<?> updateNews(@Valid @RequestBody NewsRequest news, BindingResult bindingResult){
        if (news.getId()<=0){
            bindingResult.rejectValue("id", "400", "for update entity id must be grater than 0");
        }
        if (bindingResult.hasErrors()){
            throw new BadRequestFieldException("news has errors",bindingResult);
        }
        var response=newsService.updateNews(news);
        if (response!=null){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);
        }
        throw new InternalServerException(this.getClass(),"unknown error");
    }

    @DeleteMapping("news/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id){
        return ResponseEntity.ok(newsService.deleteNews(id));
    }
}
