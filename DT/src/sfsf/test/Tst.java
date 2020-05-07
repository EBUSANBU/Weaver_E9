package sfsf.test;

import sfsf.OAJhptRemoteServiceService;
import sfsf.OAJhptRemoteServiceServiceLocator;
import sfsf.OAJhptRemoteService_PortType;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

public class Tst {
    public static void main(String[] args) throws RemoteException, ServiceException {
        // TODO Auto-generated method stub

        OAJhptRemoteServiceService o  = new OAJhptRemoteServiceServiceLocator();
        OAJhptRemoteService_PortType oa = o.getOAJhptRemoteService();
        System.out.println(oa.getAccountsJtbb());
    }

}
