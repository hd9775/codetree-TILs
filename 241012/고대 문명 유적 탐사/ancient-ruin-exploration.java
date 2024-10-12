import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	public static int K, M, index;
	public static int[] board;
	public static int[][] map;
	public static int MAP_SIZE = 5;
	
	public static class Rotate implements Comparable<Rotate> {
		int r;
		int c;
		int rot;
		int treasureCount;
		
		public Rotate(int r, int c) {
			this.r = r;
			this.c = c;
		}
		
		public Rotate(int r, int c, int rot, int treasureCount) {
			this.r = r;
			this.c = c;
			this.rot = rot;
			this.treasureCount = treasureCount;
		}

		@Override
		public int compareTo(Rotate o) {
		    if (this.treasureCount == o.treasureCount) {
		        if (this.rot == o.rot) {
		            if (this.c == o.c) {
		                return Integer.compare(this.r, o.r);
		            } else {
		                return Integer.compare(this.c, o.c);
		            }
		        } else {
		            return Integer.compare(this.rot, o.rot);
		        }
		    } else {
		        return Integer.compare(o.treasureCount, this.treasureCount);
		    }
		}
		
		@Override
		public String toString() {
			return "Rotate[r:"+r+", c:"+c+", rot:"+rot+", treasureCount:"+treasureCount+"]";
		}

	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		st = new StringTokenizer(br.readLine());
		
		K = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		map = new int[MAP_SIZE][MAP_SIZE];
		for(int i = 0; i < MAP_SIZE; i++) {
			st = new StringTokenizer(br.readLine());
			
			for(int j = 0; j < MAP_SIZE; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		board = new int[M];
		st = new StringTokenizer(br.readLine());
		for(int i = 0; i < M; i++) {
			board[i] = Integer.parseInt(st.nextToken());
		}
		
		index = 0;
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i <= K; i++) {
			
			Rotate rotate = chooseMiddlePoint();
			int tmpCount = rotate.treasureCount;
			
			replaceTreasureMap(rotate);
			
			int tmp = 0;
			do {
				tmp = findTreasure(map);
				fillBlankMap(map);
				tmpCount += tmp;
			} while (tmp != 0);
			
			if(tmpCount != 0) {
				sb.append(tmpCount + " ");
			}
		}
		
		System.out.print(sb.toString());
	}
	
	// 회전 중심 좌표 선택
	public static Rotate chooseMiddlePoint() {
		PriorityQueue<Rotate> pq = new PriorityQueue<>();
		
		for(int r = 1; r < MAP_SIZE - 1; r++) {
			for(int c = 1; c < MAP_SIZE - 1; c++) {
				for(int count = 1; count <= 3; count++) {
					int[][] tmpMap = copyMap();
					Rotate rotate = new Rotate(r, c, count, findTreasure(rotateMap(tmpMap, r, c, count)));
					pq.add(rotate);
				}
			}
		}
		
		return pq.poll();
	}
	
	public static int[][] rotateMap(int[][] map, int r, int c, int count) {
		for(int t = 0; t < count; t++) {
			int tmp = map[r-1][c-1];
			map[r-1][c-1] = map[r+1][c-1];
			map[r+1][c-1] = map[r+1][c+1];
			map[r+1][c+1] = map[r-1][c+1];
			map[r-1][c+1] = tmp;
			tmp = map[r-1][c];
			map[r-1][c] = map[r][c-1];
			map[r][c-1] = map[r+1][c];
			map[r+1][c] = map[r][c+1];
			map[r][c+1] = tmp;
		}
		
		return map;
	}
	
	public static int[][] copyMap() {
		int[][] tmpMap = new int[MAP_SIZE][MAP_SIZE];
		for(int i = 0; i < MAP_SIZE; i++) {
			for(int j = 0; j < MAP_SIZE; j++) {
				tmpMap[i][j] = map[i][j];
			}
		}
		return tmpMap;
	}

	
	// 3개 이상 연결된 유물 찾기
	public static int findTreasure(int[][] map) {
		boolean[][] visited = new boolean[MAP_SIZE][MAP_SIZE];
		
		int treasureCount = 0;
		for(int i = 0; i < MAP_SIZE; i++) {
			for(int j = 0; j < MAP_SIZE; j++) {
				// 유물이 없거나 탐색한 경우
				if(map[i][j] == 0 || visited[i][j]) {
					continue;
				}
				
				// 주변에 연결된 유물 확인
				int treasure = bfsMap(map, visited, i, j);
				
				// 3개 이상 연결되어 있으면
				if(treasure >= 3) {
					treasureCount += treasure;
				}
			}
		}
		return treasureCount;
	}
	
	public static int bfsMap(int[][] map, boolean[][] visited, int r, int c) {
		int[] dr = {0, 0, 1, -1};
		int[] dc = {1, -1, 0, 0};
		
		int treasure = map[r][c];
		int treasureCount = 0;
		
		visited[r][c] = true;
		
		Queue<Rotate> que = new ArrayDeque<>();
		Queue<Rotate> tmpQue = new ArrayDeque<>();
		que.add(new Rotate(r, c));
		tmpQue.add(new Rotate(r, c));
		while(!que.isEmpty()) {
			treasureCount++;
			
			Rotate rotate = que.poll();
			for(int i = 0; i < 4; i++) {
				int nr = rotate.r + dr[i];
				int nc = rotate.c + dc[i];
				
				if(checkInMap(nr, nc) && !visited[nr][nc]) {
					if(map[nr][nc] == treasure) {
						visited[nr][nc] = true;
						
						Rotate tmpRot = new Rotate(nr, nc); 
						
						que.add(tmpRot);
						tmpQue.add(tmpRot);
					}
				}
			}
		}
		
		if(treasureCount >= 3) {
			while(!tmpQue.isEmpty()) {
				Rotate rot = tmpQue.poll();
				map[rot.r][rot.c] = 0;
			}
		}
		
		return treasureCount;
	}
	
	public static boolean checkInMap(int r, int c) {
		if(r < 0 || c < 0 || r >= MAP_SIZE || c >= MAP_SIZE) {
			return false;
		}
		return true;
	}
	
	// 유물을 찾은 후 새롭게 채우기
	public static void replaceTreasureMap(Rotate rotate) {
		int[][] tmpMap = copyMap();
		
		findTreasure(rotateMap(tmpMap, rotate.r, rotate.c, rotate.rot));
		
		
		fillBlankMap(tmpMap);
		
		map = tmpMap;
		
	}
	
	// 새로운 유물 조각
	public static void fillBlankMap(int[][] map) {
		if(index > M) {
			return;
		}
		
		for(int j = 0; j < 5; j++) {
			for(int i = 4; i >= 0; i--) {
				if(map[i][j] == 0) {
					map[i][j] = board[index];
					index++;
					
					if(index > M) {
						return;
					}
				}
			}
		}
	}
}