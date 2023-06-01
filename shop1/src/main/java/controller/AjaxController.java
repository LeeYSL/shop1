package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* 
 *  @Controller : @Component + Controller 기능
 *     호출 되는 메서드 리턴 타입: ModelAndView : 뷰 이름 + 데이터
 *     호출 되는 메서드 리턴 타입: String       : 뷰 이름
 *     
 *  @RestController :  @Component + Controller 기능 + 클라이언트에 데이터를 직접 전달
 *  호출 되는 메서드 리턴 타입: String    : 클라이언트에 전달되는 문자열 값
 *  호출 되는 메서드 리턴 타입: Object    : 클라이언트에 전달되는 값.(JSON 형태)
 *  
 *  Spring 4.0 이후에 추가
 *  Spring 4.0 이전 버전에서는 @ResponseBody 기능으로 설정하였음
 *  @ResponseBody 어노테이션은 메서드에 설정함
 *
 */
@RestController // 뷰단 필요 없이 컨트롤러에서 직접 한다.?
@RequestMapping("ajax")
public class AjaxController {
	@RequestMapping("select")
	public List<String> select(String si, String gu, HttpServletRequest request) {
		BufferedReader fr = null;
		String path = request.getServletContext().getRealPath("/") + "file/sido.txt"; // sido.txt라는 파일을 읽어서 한 줄 씩 가져와
		try {
			fr = new BufferedReader(new FileReader(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Set : 중복 불가
		// LinkedHashset : 순서유지. 중복 불가. 리스트아님(첨자 사용 안 됨).
		Set<String> set = new LinkedHashSet<>();
		String data = null;
		if (si == null && gu == null) {
			try {
				while ((data = fr.readLine()) != null) {
					String[] arr = data.split("\\s+"); // \\s+ : 공백 한 개 이상
					// 공백으로 분리해서 배열로 만들어라
					if (arr.length >= 3)
						set.add(arr[0].trim()); // 중복 제거 됨.
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (gu == null) { // si 파라미터 존재
			si = si.trim();
			try {
				while ((data = fr.readLine()) != null) {
					String[] arr = data.split("\\s+");
					if (arr.length >= 3 && arr[0].equals(si) && !arr[1].contains(arr[0])) {
						set.add(arr[1].trim());

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			si = si.trim();
			gu = gu.trim();
			try {
				while ((data = fr.readLine()) != null) {
					String[] arr = data.split("\\s+");
					if (arr.length >= 3 && arr[0].equals(si) && arr[1].equals(gu) && !arr[0].equals(arr[1])
							&& !arr[2].contains(arr[1])) {
					if(arr.length > 3) {
						if(arr[3].contains(arr[1])) continue;
						arr[2] +=" " + arr[3];
					}
                        set.add(arr[2].trim());
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
		List<String> list = new ArrayList<>(set); // set 객체 => list 객체로 변경
		return list; // 리스트 객체가 브라우저에 전달. 뷰가 아님
						// pom.xml의 fasterxml.jackson...의 설정에 의해서 브라우저는 배열로 인식함
	}

//	@RequestMapping("select2") //클라이언트로 문자열 전송.인코딩 설정이 필요.
	/*
	 * produces : 클라이언트에 전달되는 데이터의 특징을 설정 text/plain : 데이터 특징. 순수문자.
	 * 
	 * text/html : html 형식의 문자. text/xml : xml 형식의 문자. -- 이 두개는 뷰단 만드는게 좋다?
	 * 
	 * charset=utf-8 : 한글은 utg-8로 인식
	 */
	@RequestMapping(value = "select2", produces = "text/plain; charset=utf-8")
	public String select2(String si, String gu, HttpServletRequest request) {
		BufferedReader fr = null;
		String path = request.getServletContext().getRealPath("/") + "file/sido.txt"; // sido.txt라는 파일을 읽어서 한 줄 씩 가져와
		try {
			fr = new BufferedReader(new FileReader(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Set : 중복 불가
		// LinkedHashset : 순서유지. 중복 불가. 리스트아님(첨자 사용 안 됨).
		Set<String> set = new LinkedHashSet<>();
		String data = null;
		if (si == null && gu == null) {
			try {
				while ((data = fr.readLine()) != null) {
					String[] arr = data.split("\\s+"); // \\s+ : 공백 한 개 이상
					// 공백으로 분리해서 배열로 만들어라
					if (arr.length >= 3)
						set.add(arr[0].trim()); // 중복 제거 됨.
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	List<String> list = new ArrayList<>(set); // set 객체 => list 객체로 변경
	return list.toString(); // list에 있는 내용을 문자열로 바꿔라 - ["서울특별시","경기도"....] :
}

}
