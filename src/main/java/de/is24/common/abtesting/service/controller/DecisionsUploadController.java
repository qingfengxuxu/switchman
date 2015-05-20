package de.is24.common.abtesting.service.controller;

import com.google.common.collect.Lists;
import de.is24.common.abtesting.service.domain.AbTestDecision;
import de.is24.common.abtesting.service.service.AbTestDecisionService;
import org.jsefa.csv.CsvDeserializer;
import org.jsefa.csv.CsvIOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class DecisionsUploadController {
  public static final CsvIOFactory CSV_IO_FACTORY = CsvIOFactory.createFactory(UploadedDecision.class);

  @Autowired
  private AbTestDecisionService abTestDecisionService;

  @RequestMapping(value = "/decisionsUpload", method = RequestMethod.POST)
  @Secured("ROLE_ADMIN")
  public String handleDecisionsUpload(@RequestParam("decisions") MultipartFile file,
                                      RedirectAttributes redirectAttributes) throws IOException {
    byte[] bytes = readUploadedFile(file);
    List<AbTestDecision> decisionsFromFile = deserializeContent(bytes);
    abTestDecisionService.saveBatch(decisionsFromFile);
    redirectAttributes.addAttribute("decisions",
      decisionsFromFile.stream().map(AbTestDecision::getId).collect(Collectors.joining(",")));
    redirectAttributes.addAttribute("unused", 0);
    return "redirect:/admin/decisions/decisionsFromUpload";
  }

  private List<AbTestDecision> deserializeContent(byte[] bytes) {
    CsvDeserializer deserializer = CSV_IO_FACTORY.createDeserializer();
    deserializer.open(new StringReader(new String(bytes)));

    List<AbTestDecision> decisionsFromFile = Lists.newArrayList();
    while (deserializer.hasNext()) {
      UploadedDecision uploadedDecision = deserializer.next();
      AbTestDecision abTestDecision = new AbTestDecision();
      abTestDecision.setTestName(uploadedDecision.testName);
      abTestDecision.setUserSsoId(uploadedDecision.ssoId);
      abTestDecision.setVariantId(uploadedDecision.variant);
      decisionsFromFile.add(abTestDecision);
    }
    return decisionsFromFile;
  }

  private byte[] readUploadedFile(MultipartFile file) throws IOException {
    String name = file.getName();
    byte[] bytes = file.getBytes();
    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
    stream.write(bytes);
    stream.close();
    return bytes;
  }
}
