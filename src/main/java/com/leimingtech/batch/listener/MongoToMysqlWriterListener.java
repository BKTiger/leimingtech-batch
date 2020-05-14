package com.leimingtech.batch.listener;

import com.leimingtech.batch.entity.BrandFavPO;
import com.leimingtech.batch.entity.Goods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhangtai
 * @date 2020/5/9 13:50
 * @Description:
 */
@Component
public class MongoToMysqlWriterListener implements ItemWriteListener<Goods> {

    private static final Logger log = LoggerFactory.getLogger(MongoToMysqlWriterListener.class);

    @Override
    public void beforeWrite(List<? extends Goods> list) {
        log.info("写入数据库之前");
    }

    @Override
    public void afterWrite(List<? extends Goods> list) {
        log.info("写入数据库之后");
    }

    @Override
    public void onWriteError(Exception e, List<? extends Goods> list) {
        log.error("写入数据异常:"+e.getCause());
    }
}
