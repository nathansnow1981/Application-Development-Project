package com.wilma.web.controller.web.portal;

import com.wilma.config.web.UserPortalConfiguration;
import com.wilma.entity.Frequency;
import com.wilma.entity.PayType;
import com.wilma.entity.positions.ExpressionOfInterest;
import com.wilma.entity.dto.PostDTO;
import com.wilma.entity.dto.ReplyDTO;
import com.wilma.entity.positions.Job;
import com.wilma.entity.positions.Placement;
import com.wilma.entity.positions.RequestToSupply;
import com.wilma.entity.users.Partner;
import com.wilma.service.forum.CategoryService;
import com.wilma.service.forum.ForumService;
import com.wilma.service.forum.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping ("/partner")
public class PartnerPortalController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    ForumService forumService;
    @Autowired
    TagService tagService;

    //region Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAllAttributes(Map.of(
                "currentPage", "dashboard",
                "menuElements", UserPortalConfiguration.partnerMenuElements
        ));
        return "/partner/dashboard";
    }
    //endregion

    //region Marketplace
    @GetMapping("/marketplace")
    public String marketplace(Model model) {
        model.addAllAttributes(Map.of(
                "currentPage", "marketplace",
                "menuElements", UserPortalConfiguration.partnerMenuElements,
                "partnerPositions", List.of(
                        new Job(1, new Partner("Warlock Digital", "Warlock Digital"), new Date(), new Date(), Period.of(0,6,0), "Geraldton", "Build Android version of Ill Technique App", false, false, 36.50, PayType.WAGE, Frequency.WEEKLY),
                        new Job(2, new Partner("Google", "Google"), new Date(), new Date(), Period.of(0,0,1), "Perth", "A 2nd sample job", false, true, 27.50, PayType.WAGE, Frequency.WEEKLY),
                        new Placement(3, new Partner("Apple", "Apple"), new Date(), new Date(), Period.of(1,0,0), "Sydney", "A placement example", false, true, false)
                )
        ));
        return "/partner/marketplace";
    }

    @GetMapping("/new-position")
    public String newPosition (Model model) {
        model.addAllAttributes(Map.of(
                "currentPage", "marketplace",
                "menuElements", UserPortalConfiguration.partnerMenuElements,
                "positionOptions", List.of("Select...", "Job", "Placement" )
        ));
        return "/partner/new-position";
    }

    @GetMapping("/edit-position")
    public String newPosition (Model model, @RequestParam String type) {
        model.addAllAttributes(Map.of(
                "currentPage", "marketplace",
                "menuElements", UserPortalConfiguration.partnerMenuElements,
                "positionType", type
        ));
        return "/partner/edit-position";
    }
    //endregion

    //region Forum
    @GetMapping("/forum")
    public String forumOverview(Model model) {
        model.addAllAttributes(Map.of(
                "currentPage", "forum",
                "menuElements", UserPortalConfiguration.partnerMenuElements,
                "categoryList", categoryService.findAll(),
                "recentPosts", forumService.getPosts()
        ));
        return "/partner/forum/overview";
    }

    @GetMapping("/forum-content")
    public String forumContent(@RequestParam String type, Model model, @RequestParam(required = false) Integer postId) {
        model.addAllAttributes(Map.of(
                "currentPage", "forum",
                "menuElements", UserPortalConfiguration.partnerMenuElements,
                "availableCategories", categoryService.findAll(),
                "availableTags", tagService.findAll(),
                "contentType", type,
                "post", new PostDTO(),
                "reply", new ReplyDTO()));
        if(postId != null)
            model.addAttribute("postId", postId);
        return "/partner/forum/forum-content";
    }

    @PostMapping("/reply-to-post")
    public RedirectView replyToPost(@ModelAttribute ReplyDTO replyDTO, @RequestParam Integer postId, Model model){
        var reply = forumService.addReplyFromDTO(replyDTO);
        var category = reply.getPost().getCategory().getName();
        model.addAllAttributes(Map.of(
                "currentPage", "forum",
                "menuElements", UserPortalConfiguration.partnerMenuElements,
                "postId", postId,
                "categoryName", category,
                "postsByCategory", forumService.getPostByCategoryName(category),
                "repliesForPosts", forumService.getPostRepliesByCategory(category),
                "reply", replyDTO));

        log.info("Reply added: "+ reply);
        return new RedirectView("/partner/forum-thread?category="+ category);
    }

    @PostMapping("/create-post")
    public RedirectView createPost(@ModelAttribute PostDTO postDTO, Model model){
        var newPost = forumService.addPostFromDTO(postDTO);
        var categoryName = newPost.getCategory().getName();
        model.addAllAttributes(Map.of(
                "currentPage", "forum",
                "menuElements", UserPortalConfiguration.partnerMenuElements,
                "availableCategories", categoryService.findAll(),
                "availableTags", tagService.findAll(),
                "categoryName", categoryName,
                "postsByCategory", forumService.getPostByCategoryName(categoryName),
                "repliesForPosts", forumService.getPostRepliesByCategory(categoryName),
                "post", postDTO));

        log.info("Post created from DTO: "+ newPost);
        return new RedirectView("/partner/forum");
    }

    @GetMapping("/forum-thread")
    public String forumThread(@RequestParam String category, Model model) {
        model.addAllAttributes(Map.of(
                "currentPage", "forum",
                "menuElements", UserPortalConfiguration.partnerMenuElements,
                "categoryName", category,
                "postsByCategory", forumService.getPostByCategoryName(category),
                "repliesForPosts", forumService.getPostRepliesByCategory(category)));
        return "/partner/forum/forum-thread";

    }
    //endregion

    //region Expressions of interest
    @GetMapping("/expressions-of-interest")
    public String expressionsOfInterest(Model model) {
        model.addAllAttributes(Map.of(
                "currentPage", "Expressions Of Interest",
                "menuElements", UserPortalConfiguration.partnerMenuElements,

                "openExpressionsOfInterest", List.of(
                    new ExpressionOfInterest(1,"Software Development", "Brisbane", "Slavery with extra steps", new Date(), false),
                    new ExpressionOfInterest(2,"Network", "Sydney", "A sample job", new Date(), false),
                    new ExpressionOfInterest(3,"Project Management", "Melbourne", "Another sample job", new Date(), false)
                ),
                "openRequestsToSupply", List.of(
                    new RequestToSupply(1, new Partner("Microsoft", "Microsoft"), new Date(), new Date(), Period.of(0,0,1), "Brisbane", "A sample job", false, false),
                    new RequestToSupply(2, new Partner("Google", "Google"), new Date(), new Date(), Period.of(0,11,1), "Perth", "Another sample job", false, false),
                    new RequestToSupply(3, new Partner("Apple", "Apple"), new Date(), new Date(), Period.of(1,0,0), "Sydney", "A placement example", false, false),
                    new RequestToSupply(4, new Partner("Amazon", "Amazon"), new Date(), new Date(), Period.of(1,1,1), "Melbourne", "Slavery with extra steps", false, false)
                ),
                "acceptedRequestsToSupply", List.of(
                    new RequestToSupply(1, new Partner("Microsoft", "Microsoft"), new Date(), new Date(), Period.of(0,0,1), "Brisbane", "A sample job", true, true),
                    new RequestToSupply(2, new Partner("Google", "Google"), new Date(), new Date(), Period.of(0,11,1), "Perth", "Another sample job", true, true)
                )


        ));
        return "/partner/expressions-of-interest";
    }
    //endregion

    //region Profile
    @GetMapping("/profile")
    public String partnerProfile(Model model) {
        model.addAllAttributes(Map.of(
                "currentPage", "Profile",
                "menuElements", UserPortalConfiguration.partnerMenuElements
        ));
        return "/partner/profile";
    }
    //endregion

}
