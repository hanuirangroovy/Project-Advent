import Head from "next/head";
import { useState } from "react";
import styles from '../../styles/sendbox/sendbox.module.css'
import SendboxList from "../../src/component/sendbox/sendboxList";


export default function Sendbox(){

    const [username, setUsername] = useState<string>('테스트') // 추후 axios(get)로 회원정보를 가져와 반영할 예정임

    return(
        <>
            <Head>
                <title>보낸 선물함 | Make Our Special</title>
            </Head>
            <div className={ styles.background }>
                <div 
                    className={ styles.titleWrapper } 
                    data-aos="zoom-in-right"
                >
                    <h1 className={ styles.title }>
                        <span>❝ { username } ❞님</span>의 <span>보낸 선물함</span>
                    </h1>
                </div>
                <SendboxList />
            </div>
        </>
    );
}