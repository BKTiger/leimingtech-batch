package com.leimingtech.batch.listener;

import com.leimingtech.batch.entity.Goods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhangtai
 * @date 2020/5/9 14:15
 * @Description:
 */
@Component
public class MongoToMysqlReadeListener implements ItemReadListener<Goods> {

    private static final Logger log = LoggerFactory.getLogger(MongoToMysqlReadeListener.class);


    @Override
    public void beforeRead() {
      //  log.info("读取数据之前");
    }

    @Override
    public void afterRead(Goods item) {
        log.info("读取数据之后:{}", item.toString());
    }

    @Override
    public void onReadError(Exception ex) {

        log.info("发生异常:"+ex.getCause());
    }
}
