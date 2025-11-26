package com.smartcourse.infa.tika;

import com.smartcourse.infra.tika.TikaTextExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TikaTests {
    @Autowired
    private TikaTextExtractor extractor;
    @Test
    public void tikaTest(){
       String res =  extractor.extractText("https://evaluation-expert.oss-cn-beijing.aliyuncs.com/2025/11/23/a7c3deba-4277-4c6e-82cb-ae20453b5ac0.pdf?Expires=1764137700&OSSAccessKeyId=TMP.3KmdwuQ75qQtnKrBWtfLp9a9NTfve4x4d1NHp724SWyeTEa4Qix1SVDn6fi61TzD96wA99KQSC95Q4uAPcw2v8GfiSjLtS&Signature=agD5QyzJKpFbHE0KCsYzith0ZgQ%3D");
        System.out.println(res);
    }
}
