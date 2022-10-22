package OSI.Link;

public class frameConfig {
    public static final int bitLength = 300;
    public static final float fragmentTime = 0.0001f;
    public static int fragmentLength=(int) (frameConfig.fragmentTime * 44100);;
    //#region headerData (different length)
    public static final float[] header={0f,0.283607520334602f,0.548285408538457f,0.769639178671355f,0.925154966538851f,0.996722888225197f,0.973071120022031f,0.851803284899787f,0.640717037563260f,0.358108287665182f,0.0318402487150373f,-0.302919268336715f,-0.607221555696697f,-0.842855773573199f,-0.977398086825230f,-0.989155692911442f,-0.871262830003065f,-0.634175597162449f,-0.305924795371776f,0.0702691665481909f,0.441050191829455f,0.750233027859504f,0.947480940409800f,0.997073478878382f,0.885241388221497f,0.624544660964793f,0.254063604629542f,-0.165232880092451f,-0.559740222641148f,-0.855934308047812f,-0.994497386806144f,-0.943174782973443f,-0.705523675688089f,-0.323154247802393f,0.129877124057547f,0.560306158044969f,0.874532168035663f,0.999506856629410f,0.901004149145651f,0.594585463686400f,0.145696216484992f,-0.342448046010296f,-0.751586401203641f,-0.977149851672891f,-0.956170081352601f,-0.686797460099706f,-0.233147240487961f,0.287535460227366f,0.733748429376260f,0.978114449098240f,0.945072368261817f,0.636561897164794f,0.136614993538080f,-0.408809803033025f,-0.833021673276404f,-0.999844298586301f,-0.849288593841167f,-0.421976943284209f,0.148045062689076f,0.672818452391600f,0.971773908410410f,0.935306960838217f,0.568014293334441f,-0.00614789210006842f,-0.582329783587017f,-0.946509119245941f,-0.956131974063899f,-0.599152782738216f,-0.00618042021661603f,0.593407890034118f,0.958575249095719f,0.934822673055751f,0.523265927472519f,-0.111374539746144f,-0.702636267618315f,-0.992927094741585f,-0.847842905149104f,-0.322907969280864f,0.351813330829807f,0.868922028610847f,0.983674774937957f,0.633395598157884f,-0.0231909084781779f,-0.672457397224338f,-0.993538948766584f,-0.819072741169852f,-0.227829560895710f,0.484071163045287f,0.947605705271955f,0.912918567845156f,0.389064169154804f,-0.348858014131853f,-0.899019230188528f,-0.951381670621793f,-0.467189538727530f,0.286600672089232f,0.878478137307853f,0.958510365986440f,0.470034402220895f,-0.303415233873743f,-0.896180166140179f,-0.939953795045252f,-0.397945721699721f,0.397826349721196f,0.943412343894318f,0.881951451232869f,0.243477614712873f,-0.559093119511296f,-0.991160732272224f,-0.754048701025720f,0.000650574645013664f,0.758305808478576f,0.987983984747755f,0.518545551928954f,-0.324539156248929f,-0.936110227208617f,-0.864476156292523f,-0.152225863972600f,0.676994964041106f,0.997220787457253f,0.555798418237110f,-0.317206713048203f,-0.945814184219466f,-0.829931450462047f,-0.0512428332912735f,0.771794737759365f,0.969161510114140f,0.369500876103394f,-0.539687674391071f,-0.998896278437044f,-0.612197668246032f,0.300593338293999f,0.956474356845081f,0.778555480765590f,-0.0872934629641557f,-0.878338224708940f,-0.881506427923670f,-0.0835663364068331f,0.793731904563117f,0.938715336059918f,0.206396909981046f,-0.723118487786692f,-0.966668991299440f,-0.281423268660871f,0.679098973215986f,0.977453068184641f,0.310597430323960f,-0.668063864213555f,-0.977308600949111f,-0.295065994552476f,0.691464590455782f,0.966142278109900f,0.234190971518515f,-0.746154026236165f,-0.937532556840097f,-0.126070296770188f,0.823674066093593f,0.879238562500533f,-0.0304096779637548f,-0.908756559093371f,-0.774682499062055f,0.231945035672642f,0.977774746766676f,0.606238976786214f,-0.465837249072182f,-0.998493799277625f,-0.361234529202067f,0.704647135452038f,0.933102233754720f,0.0410072326417869f,-0.902746616802210f,-0.746738471034798f,0.328198110957284f,0.998560854233301f,0.422773031065695f,-0.686490033549314f,-0.927093655165517f,0.0169791834974372f,0.941059031479617f,0.645648728025276f,-0.494539098965438f,-0.985433854137157f,-0.169498175030510f,0.874532168035669f,0.742722417678532f,-0.396841274096056f,-0.996158465323106f,-0.225548463243362f,0.858997204328822f,0.746154026236133f,-0.415921999345321f,-0.990956746727029f,-0.153961708577351f,0.905374211576198f,0.657345707770518f,-0.547986134976956f,-0.951841494158536f,0.0484488405086014f,0.978141513520857f,0.445919052858823f,-0.758539019873008f,-0.814606826651831f,0.372431020671461f,0.988813851269486f,0.0769194616766787f,-0.955778714352412f,-0.492360565571870f,0.747905632150323f,0.802686155361757f,-0.424423135766008f,-0.970658197323722f,0.0525422295095071f,0.991233943774382f,0.307318079616410f,-0.883723692231112f,-0.610344105886345f,0.681673559202488f,0.830384842360562f,-0.423362515501171f,-0.958667853036659f,0.141994317957587f,0.999981001408950f,0.159326938855725f,-0.949531289477122f,-0.464714309631581f,0.794404149118618f,0.736196820299618f,-0.534144331285201f,-0.929248409840797f,0.187038132650061f,0.999973097323692f,0.206524221582669f,-0.915554496592540f,-0.585103035257654f,0.666489062709917f,0.873726420624519f,-0.277581698873626f,-0.998781926334902f,-0.186654662145762f,0.908932954228523f,0.626929768818088f,-0.597562942595333f,-0.927726350961472f,0.119097095500546f,0.988925166339062f,0.410056248276778f,-0.764022947323708f,-0.833399399272885f,0.292080837168429f,0.999997933362878f,0.293169507249391f,-0.824024380071459f,-0.790913809460995f,0.336604143193059f,0.999737428654557f,0.298079324739017f,-0.805971898161662f,-0.824595269187597f,0.257176959048537f,0.996277172839515f,0.424069660274794f,-0.700457281591314f,-0.915515243653574f,0.0467592641884097f,0.947439320039998f,0.646741029220128f,-0.465894815516301f,-0.995338418354781f,-0.292329709472928f,0.767975061247220f,0.890657003593715f,-0.0630963326049727f,-0.939051262206011f,-0.696289843602579f,0.367293288167977f,0.998564341178560f,0.469287737139223f,-0.598970461769276f,-0.980675259044909f,-0.250884639211112f,0.758814508127515f,0.920836886606103f,0.0654984775023900f,-0.859313467587812f,-0.848635376585482f,0.0755896668220133f,0.916611028981421f,0.785448567538184f,-0.169209645230631f,-0.945221123576111f,-0.744831495540671f,0.215840147089922f,0.955154770731857f,0.733858923239116f,-0.216507098477639f,-0.950627391014333f,-0.754262315878147f,0.171229047892483f,0.929656450751727f,0.802763749331441f,-0.0789949646570235f,-0.884240758838614f,-0.870463395594165f,-0.0607262908444773f,0.801267679679656f,0.941553139932511f,0.244928633854971f,-0.664716887613376f,-0.992113315097086f,-0.462639227080070f,0.459984041252946f,0.990344427513730f,0.689888510169328f,-0.180995199794709f,-0.900155755283339f,-0.885951470519304f,-0.160129716293617f,0.690147494653901f,0.994151350405002f,0.523099597544645f,-0.348918983665565f,-0.951511836728135f,-0.834368908552522f,-0.0955535281113562f,0.710622928128894f,0.994957635505437f,0.560225326131389f,-0.272327509310036f,-0.909082084699685f,-0.908824422609212f,-0.281735394212128f,0.534611742354464f,0.986857897875537f,0.779493664148979f,0.0597846716261549f,-0.695425469125645f,-0.999761450172112f,-0.667264722400217f,0.0825938480887725f,0.777513283337802f,0.992480001216211f,0.605566152156327f,-0.141446910958608f,-0.801092486102563f,-0.990172313164383f,-0.608125741813243f,0.117611290840209f,0.773446850881612f,0.997215938368503f,0.674429208611216f,-0.0105716418034563f,-0.686111493894328f,-0.997240141604916f,-0.789478465064179f,-0.179043363915886f,0.518183927546009f,0.953385020817239f,0.917996938947221f,0.438450084007074f,-0.247450837316888f,-0.812696855918593f,-0.997446257078135f,-0.724980725340893f,-0.127554538183815f,0.522683685562606f,0.940020389820401f,0.949429205603674f,0.555636157492983f,-0.0679002428853408f,-0.659255627391181f,-0.979203907643007f,-0.905953477786652f,-0.477110876647005f,0.133295186474740f,0.688592263490063f,0.981890341382026f,0.911104058888821f,0.510456628180116f,-0.0692632328957398f,-0.620318414961244f,-0.953237670963830f,-0.960389916647623f,-0.647410649166941f,-0.124843971250497f,0.433737196493812f,0.850659545245943f,0.999951588159374f,0.842960787505262f,0.434762753696843f,-0.0978522436015974f,-0.597667266557492f,-0.923828175337330f,-0.990308318653275f,-0.786112500904288f,-0.373457205540758f,0.133424139188139f,0.601338208613419f,0.913303175300253f,0.996683299957830f,0.837990490899589f,0.482105958776359f,0.0177272317176118f,-0.446093737973870f,-0.805971898161642f,-0.986620305644602f,-0.955116234700701f,-0.724801462483350f,-0.349132365401768f,0.0921206982797603f,0.510652415954513f,0.827382321530898f,0.986731445128316f,0.965093722423962f,0.772600796511765f,0.448653830570322f,0.0527696147643107f,-0.347089256412277f,-0.686395416064912f,-0.914135900802792f,-0.999667739410247f,-0.935663291588241f,-0.737186678783272f,-0.437543564447740f,-0.0819454787888968f,0.279799736519538f,0.600402146325150f,0.841048031420215f,0.975553741516094f,0.992416158937734f,0.894790261031320f,0.698690759320758f,0.429893703073875f,0.120098248441504f,-0.197093864808544f,-0.489895389836510f,-0.731379213332630f,-0.901624123518673f,-0.988871997755670f,-0.989730759635656f,-0.908552824316948f};

    public static int headerLength=header.length;

}
