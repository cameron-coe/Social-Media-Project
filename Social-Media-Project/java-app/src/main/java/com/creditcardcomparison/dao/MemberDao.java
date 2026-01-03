package com.creditcardcomparison.dao;

import com.creditcardcomparison.model.Member;

public interface MemberDao {

    Member createNewMember(String userName, String password);

    Member getMemberByUsernameAndPassword(String userName, String password);

}
