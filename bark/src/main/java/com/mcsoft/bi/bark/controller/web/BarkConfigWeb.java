package com.mcsoft.bi.bark.controller.web;

import com.mcsoft.bi.bark.service.BarkConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by MC on 2020/8/18.
 *
 * @author MC
 */
@RequestMapping("/views/config")
@Controller
public class BarkConfigWeb {

    @Autowired
    private BarkConfigService barkConfigService;

    @RequestMapping("/")
    public String list(ModelMap map) {
        map.addAttribute("configs", barkConfigService.list());
        return "configs";
    }


}
