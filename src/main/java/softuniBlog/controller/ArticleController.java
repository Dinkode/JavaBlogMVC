package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.ArticleBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.UserRepository;

import java.security.Principal;


@Controller
@PreAuthorize("isAuthenticated()")
public class ArticleController {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;


    @Autowired
    public ArticleController(UserRepository userRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }

    @GetMapping("/article/create")
    public String articleCreate (Model model) {
        model.addAttribute("view", "articles/create-article");
        return "base-layout";

    }
    @PostMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String articleCreateConfirm(ArticleBindingModel articleBindingModel, Principal principal){

        User user = this.userRepository.findByEmail(principal.getName());
        Article articleEntity = new Article (articleBindingModel.getTitle(), articleBindingModel.getContent(), user);
        this.articleRepository.saveAndFlush(articleEntity);
        return "redirect:/";
    }
    @GetMapping("/article/{id}")
    @PreAuthorize("isAuthenticated()")
    public String articleDetails(@PathVariable(name = "id") Integer id, Model model){
        Article article = this.articleRepository.findOne(id);
        model.addAttribute("article", article);
        model.addAttribute("view", "articles/details-article");
        return "base-layout";
    }

    @GetMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String articleEdit(@PathVariable(name = "id") Integer id, Model model, Principal principal){

        Article article = this.articleRepository.findOne(id);
        if (!principal.getName().equals(article.getAuthor().getEmail())){
            return "redirect:/article/"+article.getId();
        }
        model.addAttribute("article", article);
        model.addAttribute("view", "articles/edit-article");
        return "base-layout";
    }

    @PostMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String articleEditConfirm(@PathVariable(name = "id") Integer id, ArticleBindingModel articleBindingModel){
        Article article = this.articleRepository.findOne(id);
        article.setTitle(articleBindingModel.getTitle());
        article.setContent(articleBindingModel.getContent());

        this.articleRepository.saveAndFlush(article);
        return "redirect:/";
    }

    @GetMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String articleDelete(@PathVariable(name = "id") Integer id, Model model, Principal principal){

        Article article = this.articleRepository.findOne(id);
        if (!principal.getName().equals(article.getAuthor().getEmail())){
            return "redirect:/article/"+article.getId();
        }
        model.addAttribute("article", article);
        model.addAttribute("view", "articles/delete-article");
        return "base-layout";
    }

    @PostMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String articleDeleteConfirm(@PathVariable(name = "id") Integer id){
        this.articleRepository.delete(id);
        return "redirect:/";
    }
}
