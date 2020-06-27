package com.joinbe.common.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
    public static byte[] fillExcelReport(File template, Class<?> clazz, List<?> data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, clazz).withTemplate(template).sheet().doFill(data);
        return outputStream.toByteArray();
    }

    public static byte[] fillExcelReport(InputStream template, Class<?> clazz, List<?> data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, clazz).withTemplate(template).sheet().doFill(data);
        return outputStream.toByteArray();
    }

    public static byte[] fillExcelReport(File template, List<?> data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream).withTemplate(template).sheet().doFill(data);
        return outputStream.toByteArray();
    }

    public static byte[] fillExcelReport(InputStream template, List<?> data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream).withTemplate(template).sheet().doFill(data);
        return outputStream.toByteArray();
    }

    public static byte[] fillExcelReport(InputStream template, Map<String, String> header, List<?> data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(template).build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        excelWriter.fill(data, writeSheet);
        excelWriter.fill(header, writeSheet);
        excelWriter.finish();
        return outputStream.toByteArray();
    }

    public static void readExcel(InputStream file, Class<?> head, ReadListener<?> readListener) {
        EasyExcel.read(file, head, readListener).sheet().doRead();
    }
}
