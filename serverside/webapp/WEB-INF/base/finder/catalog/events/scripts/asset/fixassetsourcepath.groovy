package asset

import org.entermediadb.asset.*
import org.openedit.Data
import org.openedit.hittracker.HitTracker

public void init()
{
	MediaArchive archive = context.getPageValue("mediaarchive");//Search for all files looking for videos
	AssetSearcher searcher = mediaarchive.getAssetSearcher();
	HitTracker hits = archive.query("asset").startsWith("sourcepath", "Archive Collection/").search();
	hits.enableBulkOperations();
		
	int saved = 0;
	List tosave = new ArrayList();
	Category root = archive.getCategorySearcher().getRootCategory();
	for(Data hit in hits)
	{
		//Asset asset = archive.getAssetSearcher().loadData(hit);
		//String path = asset.getPath();
		//Category cat = archive.getCategorySearcher().createCategoryPath(path);
		//asset.removeCategory(root);
		//asset.addCategory(cat);
		
		tosave.add(asset);
		if( tosave.size() == 1000 )
		{
			saved = saved +  tosave.size();
			log.info("deleted " + saved);
			searcher.delete(tosave);
			tosave.clear();
		}
	}
	searcher.delete(tosave);
	saved = saved +  tosave.size();
	log.info("deleted " + saved);
	
}


init();
