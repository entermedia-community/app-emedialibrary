package player;

import org.entermediadb.email.PostMail
import org.entermediadb.email.TemplateWebEmail
import org.entermediadb.upload.FileUpload
import org.entermediadb.upload.UploadRequest
import org.openedit.Data
import org.openedit.data.Searcher
import org.entermediadb.asset.*
import org.entermediadb.asset.creator.*
import org.entermediadb.asset.edit.*
import org.entermediadb.asset.episode.*
import org.entermediadb.asset.modules.*
import org.entermediadb.asset.util.*
import org.openedit.util.DateStorageUtil
import org.openedit.xml.*

import org.openedit.WebPageRequest
import org.openedit.hittracker.*
import org.openedit.page.*
import org.openedit.util.*


public void handleUpload() {
	MediaArchive archive = (MediaArchive)context.getPageValue("mediaarchive");
	FileUpload command = archive.getSearcherManager().getModuleManager().getBean("fileUpload");
	UploadRequest properties = command.parseArguments(context);
	
	if (properties == null) {
		return;
	}
	if (properties.getFirstItem() == null) {
		return;
		
	}
	
	String sourcepath = "uploaded/cms/" + context.getUserName() + "/${properties.getFirstItem().getName()}";
	Asset current = archive.getAssetBySourcePath(sourcepath);
	if(current ==  null){
		current = archive.createAsset(sourcepath);
	}
	current.setProperty("assetaddeddate", DateStorageUtil.getStorageUtil().formatForStorage(new Date()));
	Category target = archive.getCategoryArchive().createCategoryTree("submissions/${context.getUserName()}/");
	current.addCategory(target);
	archive.getCategoryArchive().saveAll();
	String path = "/WEB-INF/data/" + archive.getCatalogId()	+ "/originals/" + sourcepath + "/${properties.getFirstItem().getName()}";
	String[] fields = context.getRequestParameters("field");
	if(fields != null){
		archive.getAssetSearcher().updateData(context, fields, current);
	}
	properties.saveFileAs(properties.getFirstItem(), path, context.getUser());
	current.setPrimaryFile(properties.getFirstItem().getName());
	current.setProperty("owner", context.getUserName());
	current.setProperty("userprofile", context.getUserProfile().getId());
	current.setProperty("submittedfile", "true");
	archive.saveAsset(current, null);
	context.putPageValue("newasset", current);
	List listids = new ArrayList();
	listids.add(current.getId());
	archive.fireMediaEvent("importing/assetsuploaded",context.getUser(),current,listids);
	
	//sendEmail(context,  "ian@ijsolutions.ca, rsherkin@gmail.com", "/medcenter/client/account/uploademail.html");
	
}

protected void sendEmail(WebPageRequest context, String email, String templatePage){
	Page template = pageManager.getPage(templatePage);
	WebPageRequest newcontext = context.copy(template);
	TemplateWebEmail mailer = getMail();
	mailer.loadSettings(newcontext);
	mailer.setMailTemplatePath(templatePage);
	mailer.setRecipientsFromCommas(email);
	mailer.send();
	log.info("conversion error email sent to ${email}");
}

protected TemplateWebEmail getMail() {
	PostMail mail = (PostMail)mediaarchive.getModuleManager().getBean( "postMail");
	return mail.getTemplateWebEmail();
}

handleUpload();

