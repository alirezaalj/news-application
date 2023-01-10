package ir.alirezaalijani.news.application.domain.service;

import ir.alirezaalijani.news.application.domain.request.NewsRequest;
import ir.alirezaalijani.news.application.domain.response.NewsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Alireza Alijani : <a href="https://alirezaalijani.ir">https://alirezaalijani.ir</a>
 * @email alirezaalijani.ir@gmail.com
 * @date 12/8/2022
 */

public interface NewsService {
    Page<NewsResponse> allNews(Pageable pageable);
    NewsResponse createNews(NewsRequest news);
    NewsResponse updateNews(NewsRequest news);
    boolean deleteNews(Long id);

    NewsResponse getNews(Long id);
}
