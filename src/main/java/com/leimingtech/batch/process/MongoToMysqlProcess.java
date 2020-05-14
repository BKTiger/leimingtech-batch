package com.leimingtech.batch.process;

import com.leimingtech.batch.entity.BrandFavPO;
import com.leimingtech.batch.entity.Goods;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhangtai
 * @date 2020/5/9 09:58
 * @Description:
 */
@Component
public class MongoToMysqlProcess implements ItemProcessor<Goods, Goods> {


    @Override
    public Goods process(Goods item) throws Exception {
        return item;
    }
}
