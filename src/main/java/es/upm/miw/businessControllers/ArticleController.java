package es.upm.miw.businessControllers;

import es.upm.miw.documents.Article;
import es.upm.miw.documents.Provider;
import es.upm.miw.dtos.ArticleDto;
import es.upm.miw.exceptions.FieldAlreadyExistException;
import es.upm.miw.exceptions.NotFoundException;
import es.upm.miw.repositories.ArticleRepository;
import es.upm.miw.repositories.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ProviderRepository providerRepository;


    public ArticleDto readArticle(String code) throws NotFoundException {
        return new ArticleDto(this.articleRepository.findById(code)
                .orElseThrow(() -> new NotFoundException("Article code (" + code + ")")));
    }

    public ArticleDto createArticle(ArticleDto articleDto) throws FieldAlreadyExistException, NotFoundException {
        String code = articleDto.getCode();
        if (this.articleRepository.findById(code).isPresent()) {
            throw new FieldAlreadyExistException("Article code (" + code + ")");
        }
        int stock = (articleDto.getStock() == null) ? 10 : articleDto.getStock();
        Provider provider = null;
        if (articleDto.getProvider() != null) {
            provider = this.providerRepository.findById(articleDto.getProvider())
                    .orElseThrow(() -> new NotFoundException("Provider (" + articleDto.getProvider() + ")"));
        }
        Article article = Article.builder(code).description(articleDto.getDescription()).retailPrice(articleDto.getRetailPrice())
                .reference(articleDto.getReference()).stock(stock).provider(provider).build();
        this.articleRepository.save(article);
        return new ArticleDto(article);
    }

}
