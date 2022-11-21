package OSI.IP;

import utils.DebugHelper;

import java.util.ArrayList;

/**
 * IPv4地址类,代表了形如192.168.0.1这样的ip地址
 */
public class IPv4 {
    public IPv4() {
        this.ipsegment = new ArrayList<>(4);
    }

    public IPv4(String ip) {
        this.ipsegment = new ArrayList<>(4);
        String[] ipsegment = ip.split("\\.");
        for (String s : ipsegment) {
            this.ipsegment.add(Integer.parseInt(s));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int ips : ipsegment) {
            sb.append(ips);
            sb.append(".");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 判断两个ip地址是否相同
     * @param obj 另一个ipv4对象
     * @return 只有两个ip完全一致的时候才返回true
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IPv4)) {
            DebugHelper.log("IPv4.equals()传入的参数不是IPv4类型");
            return false;
        }
        IPv4 other = (IPv4) obj;
        for (int i = 0; i < 4; i++) {
            if (other.ipsegment.get(i) != this.ipsegment.get(i)) {
                return false;
            }
        }
        return true;
    }

    ArrayList<Integer> ipsegment;

}
