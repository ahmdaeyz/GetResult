import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(90000);
        ArrayList<String> results = new ArrayList<>();
        for(int c =1600232;c<=1600500;c++) {
            HtmlPage main = webClient.getPage("http://el-eng.ddns.net/");
            HtmlForm theForm = main.getFormByName("aspnetForm");
            HtmlInput acadmicNumEntry = theForm.getInputByName("ctl00$cntphmaster$Academic_Num");
            acadmicNumEntry.setDefaultValue(""+c);
            HtmlInput enter = theForm.getInputByName("ctl00$cntphmaster$btnGo");
            HtmlPage entryPage = enter.click();
            if (!entryPage.asText().contains(" عفوا هذا الرقم الأكاديمي غير مسجل بالكلية ")) {
                for (int j = 0; j < 27; j++) {
                    if (!entryPage.asText().contains("الاسم")) {
                        HtmlForm secondForm = entryPage.getFormByName("aspnetForm");
                        HtmlInput secondEnter = secondForm.getInputByName("ctl00$cntphmaster$Button1");
                        HtmlPage thirdPage = secondEnter.click();
                        HtmlForm fourthform = thirdPage.getFormByName("aspnetForm");
                        for (int i = 2; i < 19; i++) {
                            HtmlRadioButtonInput radioButton = null;
                            if (i < 10) {
                                radioButton = (HtmlRadioButtonInput) thirdPage.getElementById("ctl00_cntphmaster_grdquestions_ctl0" + i + "_RbQuestionaire_1");
                            } else if (i >= 10) {
                                radioButton = (HtmlRadioButtonInput) thirdPage.getElementById("ctl00_cntphmaster_grdquestions_ctl" + i + "_RbQuestionaire_1");
                            }
                            radioButton.click();
                            radioButton.setChecked(true);
                        }
                        HtmlInput save = fourthform.getInputByName("ctl00$cntphmaster$btnsave");
                        entryPage = save.click();

                    }
                }
                HtmlTable table = (HtmlTable) entryPage.getElementById("ctl00_cntphmaster_StudentGradeFormView");
                String result = "";
                for (HtmlTableRow row : table.getRows()) {
                    System.out.println("=========================");
                    for (HtmlTableCell cell : row.getCells()) {
                        result = cell.asText();
                    }
                }
                String resultNoBreaks = result.replaceAll("\n", ",").replace("المادة", "").replace("ملاحظات :", "").replace("نتيجة التدريب إن وجد:", "").replace("التقدير", "");
                results.add(resultNoBreaks);
                System.out.println(c+" was retrieved.");
                Thread.sleep(10000);
            }
        }
    printToFile(results);
    }
    private static void printToFile(ArrayList<String> results) throws IOException {
        FileWriter fw = new FileWriter(new File("/home/ahmdaeyz/results1st.txt"));
        BufferedWriter bw = new BufferedWriter(fw);
        for(String result : results){
            bw.write(result);
            bw.newLine();
        }
    }
}
