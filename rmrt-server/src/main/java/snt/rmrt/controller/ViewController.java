package snt.rmrt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/***
This ensures that the correct links are forwarded to the angular application
***/
@Controller
public class ViewController {

    @RequestMapping({"/view/{url:.*}"})
    public String redirectWithParams(@PathVariable String url) {
        return "forward:/index.html";
    }

    @RequestMapping({"/view/admin/{url:.*}"})
    public String redirectAdminWithParams(@PathVariable String url) {
        return "forward:/index.html";
    }

    @RequestMapping({"/", "/readyReckoner"})
    public String redirectIndex() {
        return "forward:/index.html";
    }

    @RequestMapping({"/readyReckoner/{url:.*}"})
    public String redirectReadyReckonerLinks() {
        return "forward:/index.html";
    }

    @RequestMapping({"/view/login"})
    public String login() {
        return "forward:/index.html";

    }

    @RequestMapping({"/reviewtool-web/{url:.*}", "/reviewtool-web"})
    public String oldLinks() {
        return "forward:/index.html";
    }

}