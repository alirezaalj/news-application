package ir.alirezaalijani.news.application.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alireza Alijani : <a href="https://alirezaalijani.ir">https://alirezaalijani.ir</a>
 * @email alirezaalijani.ir@gmail.com
 * @date 12/8/2022
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsRequest {
    private Long id;
    private String title;
    private String text;
}
