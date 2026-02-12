package vip.mcsj.www.karrefinement.utils;

import java.util.*;

public class ListUtils {
    public static List<List<String>> filterStrictMatch(
            List<String> merged,
            List<List<String>> candidates) {

        // 找出所有可能的分割方式，记录有效边界
        Set<Integer> validBoundaries = findValidBoundaries(merged, candidates);

        List<List<String>> result = new ArrayList<>();

        for (List<String> candidate : candidates) {
            if (candidate.isEmpty()) continue;

            // 在 merged 中查找这个 candidate
            int index = Collections.indexOfSubList(merged, candidate);

            // 检查起始和结束位置是否都是有效边界
            if (index != -1) {
                int endIndex = index + candidate.size();
                if (validBoundaries.contains(index) && validBoundaries.contains(endIndex)) {
                    result.add(candidate);
                }
            }
        }

        return result;
    }

    // 通过动态规划找出所有能被 candidates 完整分割的边界点
    private static Set<Integer> findValidBoundaries(List<String> merged, List<List<String>> candidates) {
        int n = merged.size();
        boolean[] reachable = new boolean[n + 1];
        reachable[0] = true;

        for (int i = 0; i <= n; i++) {
            if (!reachable[i]) continue;

            for (List<String> candidate : candidates) {
                int endIndex = i + candidate.size();
                if (endIndex <= n && merged.subList(i, endIndex).equals(candidate)) {
                    reachable[endIndex] = true;
                }
            }
        }

        Set<Integer> boundaries = new HashSet<>();
        for (int i = 0; i <= n; i++) {
            if (reachable[i]) {
                boundaries.add(i);
            }
        }
        return boundaries;
    }

    /**
     * 替换列表中所有等于指定值的行
     */
    public static List<String> replaceEquals(List<String> original,
                                             String targetValue,
                                             List<String> replacement) {
        if (original == null || replacement == null) {
            return new ArrayList<>(original);
        }

        List<String> result = new ArrayList<>();
        for (String line : original) {
            if (targetValue.equals(line)) {
                result.addAll(replacement);
            } else {
                result.add(line);
            }
        }
        return result;
    }
}
