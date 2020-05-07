/**
 * OAJhptRemoteService_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package sfsf;

public interface OAJhptRemoteService_PortType extends java.rmi.Remote {
    public java.lang.String getAccountsJtbb() throws java.rmi.RemoteException;
    public java.lang.Object[] getMainSendAndDetailList(java.lang.String fwid, java.lang.String wfxxid, java.lang.String jsdw) throws java.rmi.RemoteException;
    public java.lang.String getBDWDocId(java.lang.String jsdw) throws java.rmi.RemoteException;
    public java.lang.String getBDWDoc(java.lang.String docid) throws java.rmi.RemoteException;
    public java.lang.String getAttachmentByDocId(java.lang.String docid) throws java.rmi.RemoteException;
    public java.lang.String getAttachmentByDocId1(java.lang.String docid) throws java.rmi.RemoteException;
    public java.lang.String updateReceiveDoc(java.lang.String docid, java.lang.String jsdw) throws java.rmi.RemoteException;
    public java.lang.String updateCompleteDoc(java.lang.String docid, java.lang.String jsdw) throws java.rmi.RemoteException;
    public java.lang.String acceptDoc(java.lang.String information, java.lang.String attachment, java.lang.String jsdw) throws java.rmi.RemoteException;
    public java.lang.String acceptDoc1(java.lang.String information, java.lang.String attachment, java.lang.String jsdw, javax.activation.DataHandler[] dhandler) throws java.rmi.RemoteException;
    public java.lang.String sayHello(java.lang.String userName) throws java.rmi.RemoteException;
}
