##similarity plugin for elasticsearch 6.3

###说明
es的打分算法是由 similarity 模块实现的，自带的similarity 有 bm25(基于tf/idf), dfr, dfi, bi, 等等；  
 这些算法基本都会计算逆向词频。  
 
 而在我实际的需求中，发现逆向词频的计算产生了以下副作用：
 * 由于我搜索的内容来自不同索引不同分片， 而不同索引中，同一个词的逆向词频结果往往相差很大，导致结果中某个索引的内容打分偏高不合理；
 * 而我的搜索词包括用户输入词以及同义词，有时候同义词的逆向词频比用户原来的输入词高太多，导致结果中全是同义词的匹配结果，这也是不合理的。
 
 基于上面两点，目前我并没有太好的经验如何去使用idf(逆向词频)来进行更好的搜素区分，所以我决定把搜索时的idf打分忽略掉，以后如果有需求加入idf优化再重新考虑。
 
 
 ###实现
 elasticsearch 不同版本的插件实现模式大体上差不多，但是细节上相差十分大，目前网络上的资料都比较零散，通常如果不是刚好对应上ES的版本，一般都不能照抄。
 
 打分的思路一开始我涉猎了解大概有两个方向：
 * 自定义 Similarity 模块, 通过插件系统注入 ES;
 * 实现 SearchPlugin 插件.
 
 两个方向中，第一个方法对于我目前了解的信息是比较容易实现的，主要有两步工作：
 1. 实现Similarity， 可以看下elasticsearch 中 Similarity源码，看看有哪些方法比较重要。
 我在实现的时候直接从BM25Similarity继承了，直接重写了idf的计算方法来忽略idf；
 2. 写一个Plugin类， 这个Plugin 的作用是在 es 启动时将各种插件模块注入es；
 es 各个版本中，Plugin 注入的方式差别比较大， 0.9 版本的好像是 通过 similarityProvider 和 AbstractPlugin; 而在后面一点版本中 SimilarityProvider 和 
 AbstractPlugin 已经移处，无法用这个方法注入. 6.3.2 版本的 es 我在浏览源码时发现 Plugin 类的 onIndexModule() 方法可以注入 Similarity.
 
 
 ### 使用
 1. 构建打包，将打包后的jar插件放到 es 的 plugin 目录下的新建目录， 新建目录名随意；
 2. 同时复制resources 下的 properties 文件放到上面新建的目录；
 3. 重启es；
 4. curl "http://es.ip.com/my_index/_close"
 5. curl -XPOST -H 'application/jsonxxxx' "http://es.ip.com/my_index/_settings" -d 
 '{"index":{"similarity":{"default":{"type":"BM25_WITHOUT_IDF", "idfs":2.0}}}}';
 6. curl "http://es.ip.com/my_index/_open"；
 7. 试下搜索，现在搜索的打分算法应该用的时插件的打分算法了，即是bm25忽略idf， 其中配置上的 idfs 是idf 的替换值， 最终打分是 idfs * tfnorm