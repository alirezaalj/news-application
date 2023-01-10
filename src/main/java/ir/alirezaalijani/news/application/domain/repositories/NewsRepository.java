package ir.alirezaalijani.news.application.domain.repositories;

import ir.alirezaalijani.news.application.domain.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Alireza Alijani : <a href="https://alirezaalijani.ir">https://alirezaalijani.ir</a>
 * @email alirezaalijani.ir@gmail.com
 * @date 12/8/2022
 */
public interface NewsRepository extends JpaRepository<News,Long> {
}
