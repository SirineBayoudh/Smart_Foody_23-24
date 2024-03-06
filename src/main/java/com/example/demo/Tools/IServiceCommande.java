package com.example.demo.Tools;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author FGH
 * @param <T>
 */
public interface IServiceCommande<T> {

    void deleteOne(T t) throws SQLException;
    void deleteOne(int id) throws SQLException;

    String usernameById(int id);
    Integer nbrCommandeByClient(int id) ;

    List<Integer> selectByLikes() throws SQLException;


}
