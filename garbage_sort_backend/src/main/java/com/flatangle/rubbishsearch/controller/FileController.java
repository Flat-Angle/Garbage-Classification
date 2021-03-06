package com.flatangle.rubbishsearch.controller;

import com.flatangle.rubbishsearch.POJO.params.MultiFileContainer;
import com.flatangle.rubbishsearch.common.Result;
import com.flatangle.rubbishsearch.service.GetLabelService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/uploadImgAPP")
public class FileController {

    @Resource
    private GetLabelService getLabelService;

    /**
     *
     * @param file
     * @param request
     * @return  图片的种类
     */
    @PostMapping("/getLabel")
    public Result<?> uploadImgAPP(@RequestParam("imgFile") MultipartFile file, @RequestParam(defaultValue = "") String openID, HttpServletRequest request){
        String fileName = "";
        String fileExtension = "";
        HashMap<String,Object> result = null;
        if (file != null && !file.isEmpty()) {
            try {
                //外层文件目录
//                String targetSrc = request.getServletContext().getRealPath("/")+"files";
                String targetSrc = System.getProperty("user.dir") + "/garbage_sort_backend/src/main/resources/imageFiles/";

                fileName = file.getOriginalFilename();
                fileExtension = fileName.substring(fileName.lastIndexOf("."));
                fileName = UUID.randomUUID().toString() + fileExtension;
                File targetDir = new File(targetSrc);
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                File targetFile = new File(targetSrc, fileName);
                if (targetFile.exists()) {
                    targetFile.delete();
                }

                file.transferTo(targetFile);
                fileName = targetSrc + fileName;
                //如果图片为png，需要转换为jpg
                if(fileExtension.equals(".png")){
                    getLabelService.change2JPG(targetFile);
                    fileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".jpg";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            getLabelService.insertPicture(fileName);
            result = getLabelService.getLabel(fileName, openID); // 已修改
            return Result.success(result);
        }
        return Result.error("-1","上传图片失败");
    }

    /**
     *
     * @param file
     * @param request
     * @return 一个含有结果图片和labels的包装类
     */
    @PostMapping("/getImg_labels")
    public Result<?> uploadImgAPP2(@RequestParam("imgFile") MultipartFile file,@RequestParam(defaultValue = "") String openID, HttpServletRequest request)throws IOException {
        String fileName = "";
        String extendName = "";
        String result_img = "-1";
        List<String> labels = new ArrayList<>();
        MultiFileContainer multiFileContainer = new MultiFileContainer(result_img, labels);

        if (file != null && !file.isEmpty()) {
            try {
                //外层文件目录
//                String targetSrc = request.getServletContext().getRealPath("/")+"files";
                String targetSrc = System.getProperty("user.dir") + "/garbage_sort_backend/src/main/resources/imageFiles/";
                fileName = file.getOriginalFilename();
                fileName = fileName.substring(fileName.lastIndexOf("."));
                fileName = UUID.randomUUID().toString() + fileName;
                File targetDir = new File(targetSrc);
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                File targetFile = new File(targetSrc, fileName);
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                file.transferTo(targetFile);
                extendName = fileName;
                fileName = targetSrc + fileName;
            } catch (Exception e) {
                e.printStackTrace();
            }
            getLabelService.insertMultiPicture(fileName);
            labels = getLabelService.getLabels(fileName,openID);
            result_img = getLabelService.getMultiImg(extendName);
            multiFileContainer = new MultiFileContainer(result_img, labels);
            return Result.success(multiFileContainer);
        }
        return Result.error("-1", "上传图片失败");
    }
}
