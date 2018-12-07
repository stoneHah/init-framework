package com.stylefeng.guns.rest.modular.spider.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.PhantomJSDownloader;
import us.codecraft.webmagic.pipeline.CollectorPipeline;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by dolphineor on 2014-11-21.
 * <p>
 * 以淘宝为例, 搜索冬装的相关结果
 */
public class PhantomJSPageProcessor implements PageProcessor {

    private Site site = Site.me()
            /*.setDomain("s.taobao.com")
            .setCharset("GBK")
            .addHeader("Referer", "http://www.taobao.com/")*/
            .setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        if (page.getRawText() != null){
            String contentTitle = page.getHtml().xpath("//[@id='contentTitle']/text()").toString();
            String publicDate = page.getHtml().xpath("//[@id='tdFBRQ']/text()").toString();
            page.putField("title", contentTitle);
            page.putField("publicDate", publicDate);
            page.putField("fayuan", page.getHtml().xpath("//[@id='DivContent']/div[14]/text()").toString());
            page.putField("jd", page.getHtml().xpath("//*[@id='DivContent']/div[15]/text()").toString());
            page.putField("anhao", page.getHtml().xpath("//*[@id='DivContent']/div[16]/text()").toString());
            page.putField("html", page.getRawText());
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws Exception {
        PhantomJSDownloader phantomDownloader = new PhantomJSDownloader().setRetryNum(3);

        CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();

        Spider.create(new PhantomJSPageProcessor())
//                .addUrl(" http://wenshu.court.gov.cn/CreateContentJS/CreateContentJS.aspx?DocID=8252121f-8260-4241-b707-018d52d151ca") //%B6%AC%D7%B0为冬装的GBK编码
                .addUrl("http://wenshu.court.gov.cn/content/content?DocID=8252121f-8260-4241-b707-018d52d151ca&KeyWord=") //%B6%AC%D7%B0为冬装的GBK编码
                .setDownloader(phantomDownloader)
//                .addPipeline(collectorPipeline)
                .thread((Runtime.getRuntime().availableProcessors() - 1) << 1)
                .run();

        /*List<ResultItems> resultItemsList = collectorPipeline.getCollected();
        System.out.println(resultItemsList.get(0).get("html").toString());*/
    }

}
