package com.mcsoft.bi.bark.controller;

import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;
import com.mcsoft.bi.bark.service.BarkConfigService;
import com.mcsoft.bi.common.model.response.Response;
import com.mcsoft.bi.common.util.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Created by MC on 2020/8/18.
 *
 * @author MC
 */
@RequestMapping("/config")
@RestController
public class BarkConfigController {

    @Autowired
    private BarkConfigService barkConfigService;

    /**
     * 查
     */
    @GetMapping("/list")
    public Response<Set<SymbolBarkConfig>> list() {
        return Responses.success(barkConfigService.list());
    }

    /**
     * 增
     */
    @PostMapping
    public Response<String> add(@RequestBody SymbolBarkConfig config) {
        barkConfigService.add(config);
        return Responses.success();
    }

    /**
     * 删
     */
    @DeleteMapping
    public Response<String> del(@RequestBody SymbolBarkConfig config) {
        barkConfigService.remove(config);
        return Responses.success();
    }

}
