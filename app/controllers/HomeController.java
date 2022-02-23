package controllers;

import play.mvc.Result;

public class HomeController extends BaseController {

    public Result getPing() {
        return ok();
    }
}
