package com.leimingtech.batch.mongodb;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangtai
 * @date 2020/5/12 17:39
 * @Description:
 */
public class MongoItemBulkWriter<T> implements ItemWriter<T>, InitializingBean {

    private MongoOperations template;
    private final Object bufferKey;
    private String collection;
    private boolean delete = false;

    public MongoItemBulkWriter() {
        super();
        this.bufferKey = new Object();
    }

    /**
     * Indicates if the items being passed to the writer are to be saved or
     * removed from the data store.  If set to false (default), the items will
     * be saved.  If set to true, the items will be removed.
     *
     * @param delete removal indicator
     */
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    /**
     * Set the {@link MongoOperations} to be used to save items to be written.
     *
     * @param template the template implementation to be used.
     */
    public void setTemplate(MongoOperations template) {
        this.template = template;
    }

    /**
     * Get the {@link MongoOperations} to be used to save items to be written.
     * This can be called by a subclass if necessary.
     *
     * @return template the template implementation to be used.
     */
    protected MongoOperations getTemplate() {
        return template;
    }

    /**
     * Set the name of the Mongo collection to be written to.
     *
     * @param collection the name of the collection.
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * If a transaction is active, buffer items to be written just before commit.
     * Otherwise write items using the provided template.
     *
     * @see org.springframework.batch.item.ItemWriter#write(List)
     */
    @Override
    public void write(List<? extends T> items) throws Exception {
        if(!transactionActive()) {
            doWrite(items);
            return;
        }

        List<T> bufferedItems = getCurrentBuffer();
        bufferedItems.addAll(items);
    }

    /**
     * Performs the actual write to the store via the template.
     * This can be overridden by a subclass if necessary.
     *
     * @param items the list of items to be persisted.
     */
    protected void doWrite(List<? extends T> items) {
        if(! CollectionUtils.isEmpty(items)) {
            if(delete) {
                if(StringUtils.hasText(collection)) {
                    for (Object object : items) {
                        template.remove(object, collection);
                    }
                }
                else {
                    for (Object object : items) {
                        template.remove(object);
                    }
                }
            }
            else {
                if(StringUtils.hasText(collection)) {
                    template.insert(items,collection);
//                    for (Object object : items) {
//                        template.save(object, collection);
//                    }
                }
                else {
                    template.insert(items);
//                    for (Object object : items) {
//                        template.save(object);
//                    }
                }
            }
        }
    }

    private boolean transactionActive() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }

    @SuppressWarnings("unchecked")
    private List<T> getCurrentBuffer() {
        if(!TransactionSynchronizationManager.hasResource(bufferKey)) {
            TransactionSynchronizationManager.bindResource(bufferKey, new ArrayList<T>());

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    List<T> items = (List<T>) TransactionSynchronizationManager.getResource(bufferKey);

                    if(!CollectionUtils.isEmpty(items)) {
                        if(!readOnly) {
                            doWrite(items);
                        }
                    }
                }

                @Override
                public void afterCompletion(int status) {
                    if(TransactionSynchronizationManager.hasResource(bufferKey)) {
                        TransactionSynchronizationManager.unbindResource(bufferKey);
                    }
                }
            });
        }

        return (List<T>) TransactionSynchronizationManager.getResource(bufferKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(template != null, "A MongoOperations implementation is required.");
    }

}
