package com.leimingtech.batch.process;

import com.leimingtech.batch.entity.BrandFavPO;
import com.leimingtech.batch.entity.Goods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @author zhangtai
 * @date 2020/5/11 10:47
 * @Description:
 */
@Component
public class MysqlToElsticSearchProcess implements ItemProcessor<Goods, Goods> {

    private static final Logger log = LoggerFactory.getLogger(MysqlToElsticSearchProcess.class);


    @Override
    public Goods process(Goods item) throws Exception {
        log.info(Thread.currentThread().getName()+":MysqlToElsticSearch");
        return item;
    }
}
