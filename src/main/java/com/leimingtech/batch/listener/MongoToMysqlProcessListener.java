package com.leimingtech.batch.listener;

import com.leimingtech.batch.entity.BrandFavPO;
import com.leimingtech.batch.entity.Goods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhangtai
 * @date 2020/5/9 14:32
 * @Description:
 */
@Component
public class MongoToMysqlProcessListener implements ItemProcessListener<Goods, Goods> {

    private static final Logger log = LoggerFactory.getLogger(MongoToMysqlProcessListener.class);

    @Override
    public void beforeProcess(Goods item) {
        //log.info("处理数据前:");
    }

    @Override
    public void afterProcess(Goods item, Goods result) {
       // log.info("处理数据后:");
    }

    @Override
    public void onProcessError(Goods item, Exception e) {
        log.error("处理数据异常:",e.getCause());
    }
}
