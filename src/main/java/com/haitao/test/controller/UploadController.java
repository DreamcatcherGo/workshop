package com.haitao.test.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Description:
 * Created by 王海涛（ht.wang05@zuche.com） on 2017/3/2 18:27
 */
@Controller
@RequestMapping("/uploadController")
public class UploadController {

    String uploadPath = "/uploadFiles";
//    private static final ResourceBundle    bundle = ResourceBundle.getBundle( "config" );

    public UploadController() {
        System.out.println("TestController constructed......");
    }

    @RequestMapping(value="/upload",method = RequestMethod.POST)
    public void upload(HttpServletRequest request, HttpServletResponse response){
        System.out.println(111);
        Map<String, Object> m = new HashMap<String, Object>();
        response.setCharacterEncoding( "UTF-8" );
        Integer chunk = null; /* 分割块数 */
        Integer chunks = null; /* 总分割数 */
        String tempFileName = null; /* 临时文件名 */
        String newFileName = null; /* 最后合并后的新文件名 */
        BufferedOutputStream outputStream    = null;
        String uploadFileInfoName = null;

        /* System.out.println(FileUtils.getTempDirectoryPath()); */
//        uploadPath = request.getServletContext().getRealPath( bundle.getString( "uploadPath" ) );
        File up = new File( uploadPath );
        if ( !up.exists() )
        {
            up.mkdir();
        }

        if ( ServletFileUpload.isMultipartContent( request ) )
        {
            try {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold( 1024 );
                /* factory.setRepository(new File(repositoryPath));// 设置临时目录 */
                ServletFileUpload upload = new ServletFileUpload( factory );
                upload.setHeaderEncoding( "UTF-8" );
                /* upload.setSizeMax(5 * 1024 * 1024);// 设置附件最大大小，超过这个大小上传会不成功 */
                List<FileItem> items = upload.parseRequest( request );
                for ( FileItem item : items )
                {
                    if ( item.isFormField() ) /* 是文本域 */
                    {
                        if ( item.getFieldName().equals( "name" ) )
                        {
                            tempFileName = item.getString();
                            /* System.out.println("临时文件名：" + tempFileName); */
                        } else if ( item.getFieldName().equals( "chunk" ) )
                        {
                            chunk = Integer.parseInt( item.getString() );
                            /* System.out.println("当前文件块：" + (chunk + 1)); */
                        } else if ( item.getFieldName().equals( "chunks" ) )
                        {
                            chunks = Integer.parseInt( item.getString() );
                            /* System.out.println("文件总分块：" + chunks); */
                        }
                    } else { /* 如果是文件类型 */
                        if ( tempFileName != null ) {
                            String chunkName = tempFileName;
                            if ( chunk != null ) {
                                chunkName = chunk + "_" + tempFileName;

                                uploadFileInfoName = tempFileName + "info.txt";
                                Integer completedChunkCount = this.readChunkCount(uploadPath + "/" + uploadFileInfoName);
                                if(completedChunkCount < chunk){
                                    File savedFile = new File(uploadPath, chunkName);
                                    item.write(savedFile);
                                    this.writeChunkCount(uploadPath + "/" + uploadFileInfoName,chunk);
                                }
                            }else {
                                File savedFile = new File(uploadPath, chunkName);
                                item.write(savedFile);
                            }
                        }
                    }
                }

                newFileName = UUID.randomUUID().toString().replace( "-", "" )
                        .concat( "." )
                        .concat( FilenameUtils.getExtension( tempFileName ) );
                if ( chunk != null && chunk + 1 == chunks ) {
                    outputStream = new BufferedOutputStream(
                            new FileOutputStream( new File( uploadPath, newFileName ) ) );
                    /* 遍历文件合并 */
                    for ( int i = 0; i < chunks; i++ )
                    {
                        File tempFile = new File( uploadPath, i + "_" + tempFileName );
                        byte[] bytes = FileUtils.readFileToByteArray( tempFile );
                        outputStream.write( bytes );
                        outputStream.flush();
                        tempFile.delete();
                    }
                    outputStream.flush();

                    //删除记录上传信息的文件
                    File uploadFileInfo = new File(uploadPath + "/" + uploadFileInfoName);
                    uploadFileInfo.delete();
                }
                m.put( "status", true );
                m.put( "fileUrl", uploadPath + "/" + newFileName );
            } catch ( FileUploadException e ) {
                e.printStackTrace();
                m.put( "status", false );
            } catch ( Exception e ) {
                e.printStackTrace();
                m.put( "status", false );
            } finally {
                try {
                    response.getWriter().write( JSON.toJSONString( m ) );
                    if ( outputStream != null )
                        outputStream.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Integer readChunkCount(String fileName){
        Integer num = -1;
        File tempFile = new File( fileName );
        if ( !tempFile.exists() ){
            return num;
        }
        try {
            String count = FileUtils.readFileToString( tempFile );
            num = Integer.valueOf(count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return num;
    }

    private void writeChunkCount(String fileName,Integer chunkNum){
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();

            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(chunkNum + "");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
