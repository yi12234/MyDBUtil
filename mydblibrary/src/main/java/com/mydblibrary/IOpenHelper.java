package com.mydblibrary;

import java.util.List;

/**
 * Author: Created by Real_Man.
 * Version: 1.0
 * Data: 2016/9/29.
 * Other:
 */

public interface IOpenHelper {
    //增
    void save(Object obj);

    //删
    void delete(Class<?> table, int Id);

    //改
    void udadta(Class<?> table, Object obj, int Id);

    <T> int queryId(Class<?> table, int Id);

    //查全部
    <T> List<T> queryAll(Class<T> table);

    //查单个
    <T> List<T> query(Class<T> table, int Id);
}


