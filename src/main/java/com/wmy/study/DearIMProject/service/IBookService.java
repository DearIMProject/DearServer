package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.Book;

import java.util.List;

public interface IBookService extends IService<Book> {
    /**
     * 添加一个账本
     *
     * @param name     账本名称
     * @param desc     账本简介
     * @param token    token
     * @param bookType 账本类型
     * @return 是否成功
     */
    Book add(String name, String desc, String token, int bookType, boolean isDefault);

    /**
     * 删除账本
     *
     * @param token  token
     * @param bookId 账本Id
     * @return
     */
    boolean removeBook(String token, long bookId) throws BusinessException;

    /**
     * 根据token获取所有账本
     *
     * @param token
     * @return
     */
    List<Book> queryAllBooks(String token);

    /**
     * 更新表单信息
     *
     * @param token  token
     * @param bookId
     * @param name   名称
     * @param desc   简介
     * @return
     */
    boolean updateBook(String token, long bookId, String name, String desc)
            throws BusinessException;

    /**
     * 查询一本书
     *
     * @param token  token
     * @param bookId bookId
     * @return
     */
    Book queryBook(String token, long bookId) throws BusinessException;

    /**
     * 获取默认账本
     *
     * @param token token
     * @return 账本
     */
    Book queryDefault(String token);
}
