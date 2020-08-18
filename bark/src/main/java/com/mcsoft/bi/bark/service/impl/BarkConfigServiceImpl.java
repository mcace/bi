package com.mcsoft.bi.bark.service.impl;

import com.mcsoft.bi.bark.context.AppContext;
import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;
import com.mcsoft.bi.bark.service.BarkConfigService;
import com.mcsoft.bi.bark.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by MC on 2020/8/18.
 *
 * @author MC
 */
@Service
public class BarkConfigServiceImpl implements BarkConfigService {

    @Autowired
    private NoticeService noticeService;

    @Override
    public Set<SymbolBarkConfig> list() {
        return AppContext.currentContext().getBarkConfigs().getSymbolBarkConfigs();
    }

    @Override
    public void add(SymbolBarkConfig config) {
        AppContext.currentContext().addBarkConfig(config);
        noticeService.startNewBark(config);
    }

    @Override
    public void remove(SymbolBarkConfig config) {
        AppContext.currentContext().removeBarkConfig(config);
        noticeService.removeBark(config);
    }
}
