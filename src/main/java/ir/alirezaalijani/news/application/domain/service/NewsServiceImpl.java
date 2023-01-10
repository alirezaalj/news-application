package ir.alirezaalijani.news.application.domain.service;

import ir.alirezaalijani.news.application.domain.error.exception.EntityNotFoundException;
import ir.alirezaalijani.news.application.domain.model.News;
import ir.alirezaalijani.news.application.domain.repositories.NewsRepository;
import ir.alirezaalijani.news.application.domain.request.NewsRequest;
import ir.alirezaalijani.news.application.domain.response.NewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Alireza Alijani : <a href="https://alirezaalijani.ir">https://alirezaalijani.ir</a>
 * @email alirezaalijani.ir@gmail.com
 * @date 12/8/2022
 */
@RequiredArgsConstructor
@Service
public class NewsServiceImpl implements NewsService{

    private final NewsRepository newsRepository;

    @Override
    public Page<NewsResponse> allNews(Pageable pageable) {
        return newsRepository.findAll(pageable)
                .map(news -> NewsResponse.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .text(news.getText())
                        .createAt(news.getCreateAt())
                        .updateAt(news.getUpdateAt())
                        .build());
    }

    @Override
    public NewsResponse createNews(NewsRequest request) {
        var newNews= News.builder()
                .id(0L)
                .title(request.getTitle())
                .text(request.getText())
                .build();
        var news=newsRepository.save(newNews);
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .text(news.getText())
                .createAt(news.getCreateAt())
                .updateAt(news.getUpdateAt())
                .build();
    }

    @Override
    public NewsResponse updateNews(NewsRequest request) {
        var old =  newsRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException(this.getClass(),"news not found"));
        old.setText(request.getText());
        old.setTitle(request.getTitle());
        var news= newsRepository.save(old);
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .text(news.getText())
                .createAt(news.getCreateAt())
                .updateAt(news.getUpdateAt())
                .build();
    }

    @Override
    public boolean deleteNews(Long id) {
        newsRepository.deleteById(id);
        return true;
    }

    @Override
    public NewsResponse getNews(Long id) {
        return newsRepository.findById(id)
                .map(news ->{
                    return NewsResponse.builder()
                            .id(news.getId())
                            .title(news.getTitle())
                            .text(news.getText())
                            .createAt(news.getCreateAt())
                            .updateAt(news.getUpdateAt())
                            .build();
                }).orElse(null);
    }
}
