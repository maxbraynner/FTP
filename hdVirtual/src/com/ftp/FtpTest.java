package com.ftp;


import java.io.IOException;

public class FtpTest {

    public static void main(String[] args) throws IOException {
        ClienteFTP cl = new ClienteFTP();
        cl.conect("ftp.xpg.com.br", 21);
        cl.login("lucasolvmacedo", "59236094");
        //cl.CWD("Imagens");
        //cl.list("Imagens");
        //cl.PASV();
        cl.uploadFile("Curriculum.docx", "D:\\");
        cl.downloadFile("Curriculum.docx");
        //cl.newFolder("Imagens");
        //cl.newFolder("PDF");
        //cl.newFolder("pdfs");
        //System.out.println(cl.list(""));
        //System.out.println(cl.list("/docs"));
        //cl.downloadFile("Capa.jpg");
        //cl.uploadFile("Projeto.pdf");

    }
}
