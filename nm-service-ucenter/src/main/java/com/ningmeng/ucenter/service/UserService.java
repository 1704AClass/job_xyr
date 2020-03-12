package com.ningmeng.ucenter.service;

import com.ningmeng.framework.domain.ucenter.NmCompanyUser;
import com.ningmeng.framework.domain.ucenter.NmUser;
import com.ningmeng.framework.domain.ucenter.ext.NmUserExt;
import com.ningmeng.framework.domain.ucenter.response.UcenterCode;
import com.ningmeng.framework.exception.CustomExceptionCast;
import com.ningmeng.ucenter.dao.NmCompanyUserRepository;
import com.ningmeng.ucenter.dao.NmUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private NmUserRepository nmUserRepository;

    @Autowired
    private NmCompanyUserRepository nmCompanyUserRepository;


    //根据账号查询用户的信息，返回用户扩展信息
    public NmUserExt getUserExt(String username){
        NmUser nmUser = nmUserRepository.findNmUserByUsername(username);
        if(nmUser == null){
            CustomExceptionCast.cast(UcenterCode.UCENTER_ACCOUNT_NOTEXISTS);
        }

        //获得用户id
        String userId = nmUser.getId();
        //查询用户所属公司
        NmCompanyUser nmCompanyUser = nmCompanyUserRepository.findByUserId(userId);
        NmUserExt nmUserExt = new NmUserExt();
        BeanUtils.copyProperties(nmUser,nmUserExt);
        nmUserExt.setCompanyId(nmCompanyUser.getCompanyId());

        return nmUserExt;
    }
}