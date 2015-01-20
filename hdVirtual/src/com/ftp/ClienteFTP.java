package com.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class ClienteFTP {

    private Socket cmdSock, dataSock;
    private InputStream cmdIn, dataIn;
    private OutputStream cmdOut, dataOut;
    private String host;
    private int port;

    //Espera resposta do servidor
    private String getCmdResp() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(this.cmdIn));
        String resp = br.readLine();
        System.out.println(resp);
        return resp;
    }

    public String list(String path) throws IOException {
        this.PASV();
        String msg = "LIST " + path + "\r\n";
        this.cmdOut.write(msg.getBytes());
        this.getCmdResp();
        BufferedReader br = new BufferedReader(new InputStreamReader(this.cmdSock.getInputStream()));
        String resp = "";
        String line;
        while ((line = br.readLine()) != null) {
            resp = resp + line + "\n";
        }
        this.cmdSock.close();
        this.getCmdResp();
        System.out.println(resp);
        return resp;
    }

    public void PASV() throws UnknownHostException, IOException {
        String msg = "PASV\r\n";
        this.cmdOut.write(msg.getBytes());
        String resp = this.getCmdResp();

        StringTokenizer st = new StringTokenizer(resp);
        st.nextToken("(");
        String ip = st.nextToken(",").substring(1) + "."
                + st.nextToken(",") + "."
                + st.nextToken(",") + "."
                + st.nextToken(",");
        int value1 = Integer.parseInt(st.nextToken(","));
        int value2 = Integer.parseInt(st.nextToken(")").substring(1));
        int port = value1 * 256 + value2;

        this.dataSock = new Socket(ip, port);
        this.dataIn = this.dataSock.getInputStream();
        this.dataOut = this.dataSock.getOutputStream();
    }

    public void newFolder(String nome) throws UnknownHostException, IOException {
        String msg = "MKD " + nome + "\r\n";
        this.cmdOut.write(msg.getBytes());
        this.getCmdResp();
    }

    public void CWD/*changeWorkingDirectory*/(String newDir) throws UnknownHostException, IOException {
        String msg = "CWD " + newDir + "\r\n";
        this.cmdOut.write(msg.getBytes());
        this.getCmdResp();
    }

    public String PWD()/*printWorkingDirectory*/ throws IOException {
        String msg = "PWD\r\n";
        this.cmdOut.write(msg.getBytes());
        return this.getCmdResp();
    }

    public void conect(String host, int port) throws UnknownHostException, IOException {
        this.host = host;
        this.port = port;
        this.cmdSock = new Socket(host, port);
        this.cmdIn = cmdSock.getInputStream();
        this.cmdOut = cmdSock.getOutputStream();
        this.getCmdResp();
    }

    public void login(String user, String pass) throws IOException {
        String comand = "USER " + user + "\r\n";
        this.cmdOut.write(comand.getBytes());
        this.getCmdResp();
        comand = "PASS " + pass + "\r\n";
        this.cmdOut.write(comand.getBytes());
        this.getCmdResp();
    }

    public void downloadFile(String nome) throws UnknownHostException, IOException {
        this.PASV();
        this.changeType("I");
        this.RETR(nome);
        File file = new File(nome);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buf = new byte[100];
        int tam;
        while ((tam = this.dataIn.read(buf)) != -1) {
            fos.write(buf, 0, tam);
        }
        fos.flush();
        fos.close();
        this.dataOut.close();
        System.out.println("Download concluido!");

    }

    public void uploadFile(String nome, String arquivo) throws UnknownHostException, IOException {
        this.PASV();
        this.changeType("I");
        this.STOR(nome);
        File file = new File(arquivo + nome);
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[100];
        int tam;
        while ((tam = fis.read(buf)) > -1) {
            dataOut.write(buf, 0, tam);
        }
        this.dataOut.flush();
        this.dataOut.close();
        fis.close();
        this.dataSock.close();
        this.getCmdResp();
        System.out.println("Upload realizado com Sucesso");
    }

    private void changeType(String type) throws IOException {
        String msg = "TYPE " + type + "\r\n";
        this.cmdOut.write(msg.getBytes());
        this.getCmdResp();
    }

    public void STOR(String fileName) throws IOException {
        String msg = "STOR " + fileName + "\r\n";
        this.cmdOut.write(msg.getBytes());
        this.getCmdResp();
    }

    public void RETR(String retr) throws IOException {
        String msg = "RETR " + retr + "\r\n";
        this.cmdOut.write(msg.getBytes());
        this.getCmdResp();
    }
}